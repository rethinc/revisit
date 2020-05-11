package ch.rethinc.gastrocheckin.secretstore

import android.content.Context
import ch.rethinc.gastrocheckin.GastroCheckinKeyStore
import ch.rethinc.store.AesSymmetricCipher
import ch.rethinc.store.SymmetricCipher
import ch.rethinc.store.SymmetricKeyProvider
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class GastroCheckinEncryptor(
    private val gastroCheckinKeyStore: GastroCheckinKeyStore
) {

    companion object {
        fun createInstance(context: Context): GastroCheckinEncryptor =
            GastroCheckinEncryptor(GastroCheckinKeyStore.getInstance(context))
    }

    fun encrypt(cleartext: String): String? {
        val secretKey = gastroCheckinKeyStore.secretKey
            ?: throw IllegalStateException("No secret key defined")
        return encrypt(cleartext, secretKey)
    }

    fun decrypt(input: String): String? {
        val secretKey = gastroCheckinKeyStore.secretKey
            ?: throw IllegalStateException("No secret key defined")
        return decrypt(input, secretKey)
    }

    fun encrypt(cleartext: String, key: ByteArray): String? {
        try {
            val cipher = createCipher(key)
            return cipher.encrypt(cleartext)
        } catch (t: Throwable) {
            return null
        }
    }

    fun decrypt(input: String, key: ByteArray): String? {
        try {
            val cipher = createCipher(key)
            return cipher.decrypt(input)
        } catch (t: Throwable) {
            return null
        }
    }

    private fun createCipher(key: ByteArray): SymmetricCipher {
        val keyProvider = object : SymmetricKeyProvider {
            override fun getOrCreateSecretKey(): SecretKey {
                return SecretKeySpec(key, 0, key.size, "AES")
            }

            override fun removeKey() {
            }
        }

        return AesSymmetricCipher(keyProvider)
    }
}