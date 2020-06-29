package io.mustelidae.weasel.security.domain.token

// License by https://github.com/phxql/kotlin-crypto-example/blob/master/src/main/kotlin/de/mkammerer/Crypto.kt

import io.mustelidae.weasel.security.domain.token.utils.Jackson
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal class Crypto(
    private val key: String
) {

    private val iv = "IXplYXNRZmOzKmbw".toByteArray()

    fun enc(valueType: Any): String {
        val json = Jackson.getMapper().writeValueAsString(valueType)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(this.enc(json))
    }

    private fun enc(plaintext: String): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val keySpec = SecretKeySpec(key.toByteArray(), "AES")

        val ivSpec = IvParameterSpec(iv)

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

        return cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
    }

    fun <T> dec(encryptedText: String, valueType: Class<T>): T {
        val cipherText = Base64.getUrlDecoder().decode(encryptedText)
        val json = this.dec(cipherText)
        return Jackson.getMapper().readValue(json, valueType)
    }

    private fun dec(cipherText: ByteArray): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val keySpec = SecretKeySpec(key.toByteArray(), "AES")
        val ivSpec = IvParameterSpec(iv)

        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

        return String(cipher.doFinal(cipherText), Charsets.UTF_8)
    }
}
