package com.example.neurohelp.auth

import android.graphics.*
import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.ByteArrayInputStream
import android.net.Uri
import android.util.Base64
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.neurohelp.R
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream

object ProfilePhoto {
    // Compartilhado apenas em memória e separado por sessão; a persistência é da API.
    private data class OwnPhoto(val session: String? = null, val bitmap: Bitmap? = null)
    private val ownPhoto = MutableStateFlow(OwnPhoto())
    private val revision = java.util.concurrent.atomic.AtomicLong(0)

    fun clear() {
        revision.incrementAndGet()
        ownPhoto.value = OwnPhoto()
    }

    private fun render(view: ImageView, bitmap: Bitmap?) {
        view.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setOval(0, 0, view.width, view.height)
            }
        }
        view.clipToOutline = true
        view.imageTintList = null
        view.scaleType = ImageView.ScaleType.CENTER_CROP
        if (bitmap == null) view.setImageResource(R.drawable.avatar_verde) else view.setImageBitmap(bitmap)
    }
    fun own(fragment: Fragment, view: ImageView) {
        val session = SessionStore(fragment.requireContext())
        val token = session.token()
        if (token != null && view.getTag(R.id.tag_photo_binding) == token) return
        render(view, ownPhoto.value.takeIf { token != null && it.session == token }?.bitmap)
        if (token == null) return
        view.setTag(R.id.tag_photo_binding, token)
        val service = ApiService(session)
        fragment.viewLifecycleOwner.lifecycleScope.launch {
            fragment.viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val startedAt = revision.get()
                launch {
                    try {
                        val data = photoData(service.protectedRequest("/api/conta/foto"))
                        val bitmap = withContext(Dispatchers.Default) { decode(data) }
                        // Não restaurar uma foto antiga após salvar uma nova ou trocar de conta.
                        if (startedAt == revision.get() && session.token() == token) {
                            ownPhoto.value = OwnPhoto(token, bitmap)
                        }
                    } catch (e: CancellationException) { throw e }
                    catch (_: Exception) { /* Mantém a foto já carregada se houver falha de rede. */ }
                }
                ownPhoto.collect { photo ->
                    if (session.token() == token && photo.session == token) render(view, photo.bitmap)
                    else if (photo.session == null) render(view, null)
                }
            }
        }
    }
    fun professional(fragment: Fragment, view: ImageView, path: String?) {
        render(view, null)
        if (path.isNullOrBlank()) return
        val service = ApiService(SessionStore(fragment.requireContext()))
        fragment.viewLifecycleOwner.lifecycleScope.launch {
            try {
                val bytes = service.professionalPhoto(path)
                val bitmap = withContext(Dispatchers.Default) { BitmapFactory.decodeByteArray(bytes, 0, bytes.size) }
                render(view, bitmap)
            } catch (e: CancellationException) { throw e } catch (_: Exception) { render(view, null) }
        }
    }
    fun decode(data: String?): Bitmap? {
        if (data == null || !data.startsWith("data:image/jpeg;base64,") || data.length > 350000) return null
        return try {
            val bytes = Base64.decode(data.substringAfter(','), Base64.DEFAULT)
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
            if (bounds.outWidth !in 1..1024 || bounds.outHeight !in 1..1024) null
            else BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (_: Exception) { null }
    }
    suspend fun upload(fragment: Fragment, uri: Uri): Bitmap {
        val context = fragment.requireContext().applicationContext
        val session = SessionStore(context)
        val token = session.token() ?: throw ApiException(401, "Entre novamente para alterar a foto.")
        val service = ApiService(session)
        val data = withContext(Dispatchers.IO) {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytesLimited(15 * 1024 * 1024) }
                ?: throw IllegalArgumentException("Não foi possível abrir a imagem.")
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
            require(options.outWidth > 0 && options.outHeight > 0) { "Não foi possível ler esta imagem. Escolha outra foto." }
            require(options.outWidth.toLong() * options.outHeight <= 100000000) { "A resolução da imagem é muito grande. Escolha uma foto menor." }
            options.inJustDecodeBounds = false
            options.inSampleSize = 1
            while (maxOf(options.outWidth, options.outHeight) / options.inSampleSize > 1024) {
                options.inSampleSize *= 2
            }
            val decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                ?: throw IllegalArgumentException("Imagem inválida.")
            val orientation = try {
                android.media.ExifInterface(ByteArrayInputStream(bytes)).getAttributeInt(
                    android.media.ExifInterface.TAG_ORIENTATION, android.media.ExifInterface.ORIENTATION_NORMAL)
            } catch (_: Exception) { android.media.ExifInterface.ORIENTATION_NORMAL }
            val matrix = Matrix().apply {
                when (orientation) {
                    android.media.ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> setScale(-1f, 1f)
                    android.media.ExifInterface.ORIENTATION_ROTATE_180 -> setRotate(180f)
                    android.media.ExifInterface.ORIENTATION_FLIP_VERTICAL -> setScale(1f, -1f)
                    android.media.ExifInterface.ORIENTATION_TRANSPOSE -> { setRotate(90f); postScale(-1f, 1f) }
                    android.media.ExifInterface.ORIENTATION_ROTATE_90 -> setRotate(90f)
                    android.media.ExifInterface.ORIENTATION_TRANSVERSE -> { setRotate(270f); postScale(-1f, 1f) }
                    android.media.ExifInterface.ORIENTATION_ROTATE_270 -> setRotate(270f)
                }
            }
            val source = Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)
            if (source !== decoded) decoded.recycle()
            val side = minOf(source.width, source.height)
            val avatar = Bitmap.createBitmap(256, 256, Bitmap.Config.RGB_565)
            Canvas(avatar).apply {
                drawColor(Color.WHITE)
                drawBitmap(source, Rect((source.width - side) / 2, (source.height - side) / 2,
                    (source.width + side) / 2, (source.height + side) / 2), Rect(0, 0, 256, 256),
                    Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
            }
            val output = ByteArrayOutputStream()
            avatar.compress(Bitmap.CompressFormat.JPEG, 85, output)
            source.recycle(); avatar.recycle()
            "data:image/jpeg;base64," + Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
        }
        if (session.token() != token) throw CancellationException("A sessão mudou.")
        revision.incrementAndGet()
        val confirmed = service.saveAccountPhoto(data)
        val bitmap = withContext(Dispatchers.Default) { decode(confirmed) }
            ?: throw PhotoConfirmationException()
        if (session.token() != token) throw CancellationException("A sessão mudou.")
        revision.incrementAndGet()
        ownPhoto.value = OwnPhoto(token, bitmap)
        return bitmap
    }
}
