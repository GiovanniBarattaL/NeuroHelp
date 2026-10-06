package com.example.neurohelp.auth

import org.junit.Assert.*
import org.junit.Test

class PhotoLimitsTest {
    @Test fun acceptsBytesWithinLimit() {
        val bytes = ByteArray(128) { it.toByte() }
        assertArrayEquals(bytes, bytes.inputStream().readBytesLimited(128))
    }
    @Test(expected = IllegalArgumentException::class)
    fun rejectsBytesAboveLimit() {
        ByteArray(129).inputStream().readBytesLimited(128)
    }
    @Test fun acceptsEmptyStreamForCallerValidation() {
        assertEquals(0, ByteArray(0).inputStream().readBytesLimited(128).size)
    }
}
