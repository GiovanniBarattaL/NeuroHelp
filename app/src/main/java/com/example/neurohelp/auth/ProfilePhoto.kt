package com.example.neurohelp.auth

import android.graphics.*;
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
    private fun render(view: ImageView, bitmap: Bitmap?) {
        view.imageTintList = null
        view.scaleType = ImageView.ScaleType.CENTER_CROP
        if (bitmap == null) view.setImageResource(R.drawable.avatar_verde) else view.setImageBitmap(bitmap)
    }
    fun own(fragment: Fragment, view: ImageView) {
        val service = ApiService(SessionStore(fragment.requireContext()))
        render(view, null)
        fragment.viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = JSONObject(service.protectedRequest("/api/conta/foto"))
                render(view, decode(result.optString("fotoPerfil")))
            } catch (e: CancellationException) { throw e } catch (_: Exception) { render(view, null) }
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
    suspend fun upload(fragment: Fragment, uri: Uri): Bitmap? {
        val context = fragment.requireContext().applicationContext
        val service = ApiService(SessionStore(context))
        val data = withContext(Dispatchers.IO) {
            require(context.contentResolver.getType(uri) in setOf("image/jpeg", "image/png", "image/webp"))
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytesLimited(5 * 1024 * 1024) }
                ?: throw IllegalArgumentException("Não foi possível abrir a imagem.")
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
            require(options.outWidth > 0 && options.outHeight > 0 && options.outWidth.toLong() * options.outHeight <= 24000000)
            options.inJustDecodeBounds = false
            options.inSampleSize = maxOf(1, maxOf(options.outWidth, options.outHeight) / 1024)
            val source = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                ?: throw IllegalArgumentException("Imagem inválida.")
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
        val result = JSONObject(service.protectedRequest("/api/conta/foto", "PUT", JSONObject().put("fotoPerfil", data)))
        return decode(result.optString("fotoPerfil"))
    }
}
