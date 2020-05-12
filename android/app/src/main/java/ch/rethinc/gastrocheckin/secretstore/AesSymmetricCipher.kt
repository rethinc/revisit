package ch.rethinc.store

import android.util.Base64
import android.util.Base64.NO_WRAP
import java.security.Key
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.spec.IvParameterSpec

class AesSymmetricCipher(
    private val symmetricKeyProvider: SymmetricKeyProvider
) : SymmetricCipher {

    companion object {
        private const val transformation = "AES/CBC/PKCS7Padding"
        private val initializationVectorLength: Int = 16
    }

    override fun encrypt(value: String): String {
        val initializationVector = generateInitializationVector()
        val encodedBytes = doFinalCipher(
            input = value.toByteArray(),
            key = symmetricKeyProvider.getOrCreateSecretKey(),
            mode = ENCRYPT_MODE,
            initializationVector = initializationVector
        )

        val encodedCipherText = Base64.encodeToString(encodedBytes, NO_WRAP)
        val encodedInitializationVector = Base64.encodeToString(initializationVector, NO_WRAP)

        return "$encodedInitializationVector#$encodedCipherText"
    }

    override fun decrypt(ciphertext: String?): String? {
        if (ciphertext == null) {
            return null
        }
        val tokens = ciphertext.split('#')
        if (tokens.size != 2) {
            throw Exception("Could not decrypt string")
        }
        val initializationVector = Base64.decode(tokens[0], NO_WRAP)
        val ciphertextData = Base64.decode(tokens[1], NO_WRAP)

        val decodedBytes = doFinalCipher(
            input = ciphertextData,
            key = symmetricKeyProvider.getOrCreateSecretKey(),
            mode = DECRYPT_MODE,
            initializationVector = initializationVector
        )
        return String(decodedBytes)
    }

    override fun removeKey() {
        symmetricKeyProvider.removeKey()
    }

    private fun doFinalCipher(
        input: ByteArray,
        key: Key,
        mode: Int,
        initializationVector: ByteArray
    ): ByteArray {
        val iv = IvParameterSpec(initializationVector)
        val cipher = Cipher.getInstance(transformation)
        cipher.init(mode, key, iv)

        return cipher.doFinal(input)
    }

    private fun generateInitializationVector(): ByteArray {
        val secureRandom = SecureRandom()
        val iv = ByteArray(initializationVectorLength)
        secureRandom.nextBytes(iv)
        return iv
    }

}