package ch.rethinc.store

import android.content.Context
import android.util.Base64
import android.util.Base64.DEFAULT
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class SymmetricKeyProviderAndroid(
    private val asymmetricCipher: AsymmetricCipher,
    private val keyStoreName: String,
    context: Context
) : SymmetricKeyProvider {

    companion object {
        private const val encryptionKeyName = "EncryptedPrefStoreEncryptionKey"
        private const val secretKeyAlgorithm = "AES"
    }

    private val preferences = context.getSharedPreferences(
        this.keyStoreName,
        Context.MODE_PRIVATE
    )

    override fun getOrCreateSecretKey(): SecretKey {
        val secretKey = getSecretKey()
        if (secretKey != null) {
            return secretKey
        }
        return createSecretKey()
    }

    override fun removeKey() {
        this.preferences.edit().remove(encryptionKeyName).apply()
        this.asymmetricCipher.removeKeyPair()
    }

    private fun getSecretKey(): SecretKey? {
        val encryptedKey = preferences.getString(encryptionKeyName, null) ?: return null
        return decrypt(encryptedKey)
    }

    private fun createSecretKey(): SecretKey {
        val key = generateSecretKey()
        preferences
            .edit()
            .putString(encryptionKeyName, encrypt(key))
            .apply()
        return key
    }

    private fun encrypt(key: SecretKey): String =
        Base64.encodeToString(asymmetricCipher.encrypt(key.encoded), DEFAULT)

    private fun decrypt(encryptedKey: String): SecretKey =
        SecretKeySpec(
            asymmetricCipher.decrypt(Base64.decode(encryptedKey, DEFAULT)),
            secretKeyAlgorithm
        )

    private fun generateSecretKey(): SecretKey {
        val outputKeyLength = 256
        val secureRandom = SecureRandom()
        val keyGenerator: KeyGenerator = KeyGenerator.getInstance(secretKeyAlgorithm)
        keyGenerator.init(outputKeyLength, secureRandom)
        return keyGenerator.generateKey()
    }

}