package ch.rethinc.store

import android.content.Context
import android.content.Context.MODE_PRIVATE
import ch.rethinc.gastrocheckin.secretstore.SecretEncryptionException
import ch.rethinc.gastrocheckin.secretstore.SecretsStore
import ch.rethinc.store.AsymmetricKeyProviderAndroid.Algorithm.RSA


/**
 * Some inspiration from
 * https://medium.com/@ericfu/securely-storing-secrets-in-an-android-application-501f030ae5a3
 */
class EncryptedSharedPreferencesStore(
    sharedPreferencesStoreName: String,
    context: Context,
    private val symmetricCipher: SymmetricCipher
) : SecretsStore {

    companion object {
        private const val secretPrefix = "secret_"

        fun createInstance(name: String, context: Context): EncryptedSharedPreferencesStore {
            val asymmetricCipher = RsaAsymmetricCipher(
                asymmetricKeyProvider = AsymmetricKeyProviderAndroid(
                    keyType = RSA,
                    keyAlias = "${name}.key",
                    context = context
                )
            )
            val symmetricKeyProvider = SymmetricKeyProviderAndroid(
                asymmetricCipher = asymmetricCipher,
                keyStoreName = "${name}.symmetricKey",
                context = context
            )
            return EncryptedSharedPreferencesStore(
                sharedPreferencesStoreName = name,
                context = context,
                symmetricCipher = AesSymmetricCipher(symmetricKeyProvider)
            )
        }
    }

    private val preferences =
        context.getSharedPreferences(
            sharedPreferencesStoreName,
            MODE_PRIVATE
        )

    override fun secretForKey(key: String): String? {
        return decrypt(
            preferences.getString(key, null)
        )
    }

    override fun storeSecretForKey(key: String, secret: String) {
        preferences
            .edit()
            .putString(key, encrypt(secret))
            .apply()
    }

    override fun removeSecretForKey(key: String) {
        preferences.edit().remove(key).apply()
    }

    override fun clear() {
        preferences.edit().clear().apply()
        symmetricCipher.removeKey()
    }

    override fun all(): Map<String, String?> {
        return preferences
            .all
            .mapValues { entry ->
                decrypt(entry.value as String)
            }
    }

    private fun encrypt(value: String): String {
        return symmetricCipher.encrypt(value.addPrefix(secretPrefix))
    }

    private fun decrypt(value: String?): String? {
        try {
            return symmetricCipher.decrypt(value).verifyAndRemovePrefix(secretPrefix)
        } catch (e: Exception) {
            throw SecretEncryptionException()
        }
    }

    private fun String.addPrefix(prefix: String) =
        "${prefix}${this}"

    // Adding a prefix before encrypting a value and removing it after decryption
    // ensures that the secret key has not changed in the meantime and the
    // client does not get a scrambled secret because of that.
    private fun String?.verifyAndRemovePrefix(prefix: String): String? {
        if (this == null) {
            return null
        }

        if (!this.startsWith(prefix)) {
            throw SecretEncryptionException()
        }

        return this.removePrefix(prefix)
    }
}