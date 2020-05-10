package ch.rethinc.gastrocheckin

import android.content.Context
import ch.rethinc.gastrocheckin.secretstore.SecretsStore
import ch.rethinc.store.EncryptedSharedPreferencesStore

class GastroCheckinKeyStore(
    private val secretsStore: SecretsStore
) {

    companion object {
        private const val key = "ch.rethinc.gastrocheckin.secretstore.key"

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

    var secretKey: String?
        get() = secretsStore.secretForKey(key)
        set(value) {
            if (value != null) {
                secretsStore.storeSecretForKey(key, value)
            } else {
                secretsStore.removeSecretForKey(key)
            }
        }

}