package ch.rethinc.gastrocheckin

import android.content.Context
import android.util.Base64
import android.util.Base64.NO_WRAP
import ch.rethinc.gastrocheckin.secretstore.SecretsStore
import ch.rethinc.store.EncryptedSharedPreferencesStore

class GastroCheckinKeyStore(
    private val secretsStore: SecretsStore
) {

    companion object {
        private const val secretKeyKey = "ch.rethinc.gastrocheckin.secretstore.key"
        private const val userPasswordKey = "ch.rethinc.gastrocheckin.secretstore.password"

        private var instance: GastroCheckinKeyStore? = null

        fun getInstance(context: Context): GastroCheckinKeyStore {
            synchronized(this) {
                val currentInstance = this.instance
                if (currentInstance != null) {
                    return currentInstance
                }
                val newInstance = GastroCheckinKeyStore(
                    EncryptedSharedPreferencesStore
                        .createInstance(
                            name = "ch.rethinc.gastrocheckin.secretstore",
                            context = context
                        )
                )
                instance = newInstance
                return newInstance
            }

        }
    }

    var userPasssword: String?
        get() = secretsStore.secretForKey(userPasswordKey)
        set(value) {
            if (value != null) {
                secretsStore.storeSecretForKey(userPasswordKey, value)
            } else {
                secretsStore.removeSecretForKey(userPasswordKey)
            }
        }

    var secretKey: ByteArray?
        get() {
            val secretKey = secretsStore.secretForKey(secretKeyKey) ?: return null
            return Base64.decode(secretKey, NO_WRAP)
        }
        set(value) {
            if (value != null) {
                secretsStore.storeSecretForKey(secretKeyKey, Base64.encodeToString(value, NO_WRAP))
            } else {
                secretsStore.removeSecretForKey(secretKeyKey)
            }
        }

    fun clear() {
        secretsStore.clear()
    }
}