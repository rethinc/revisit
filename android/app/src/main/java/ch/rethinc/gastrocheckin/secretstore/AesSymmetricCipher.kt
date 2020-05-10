package ch.rethinc.store

import android.util.Base64
import android.util.Base64.DEFAULT
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.spec.IvParameterSpec

class AesSymmetricCipher(
    private val symmetricKeyProvider: SymmetricKeyProvider
) : SymmetricCipher {

    companion object {
        private const val transformation = "AES/CBC/PKCS7Padding"
        private val initializationVector: ByteArray = "ZEYcDTT53t55V3e4".toByteArray()
    }

    override fun encrypt(value: String): String {
        val encodedBytes = doFinalCipher(
            input = value.toByteArray(),
            key = symmetricKeyProvider.getOrCreateSecretKey(),
            mode = ENCRYPT_MODE
        )
        return Base64.encodeToString(encodedBytes, DEFAULT)
    }

    override fun decrypt(value: String?): String? {
        if (value == null) {
            return null
        }
        val encryptedDecodedData = Base64.decode(value, DEFAULT)
        val decodedBytes = doFinalCipher(
            input = encryptedDecodedData,
            key = symmetricKeyProvider.getOrCreateSecretKey(),
            mode = DECRYPT_MODE
        )
        return String(decodedBytes)
    }

    override fun removeKey() {
        symmetricKeyProvider.removeKey()
    }

    private fun doFinalCipher(input: ByteArray, key: Key, mode: Int): ByteArray {
        val iv = IvParameterSpec(initializationVector)
        val cipher = Cipher.getInstance(transformation)
        cipher.init(mode, key, iv)

        return cipher.doFinal(input)
    }

}