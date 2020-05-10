package ch.rethinc.gastrocheckin

import ch.rethinc.gastrocheckin.secretstore.SecretsStore

class GastroCheckinKeyStore(
    private val secretsStore: SecretsStore
) {

    companion object {
        private const val key = "ch.rethinc.gastrocheckin.secretstore.key"
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