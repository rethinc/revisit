package ch.rethinc.gastrocheckin.secretstore

class SecretsStoreInMemory : SecretsStore {

    private val secrets = mutableMapOf<String, String?>()

    override fun secretForKey(key: String): String? =
        secrets[key]

    override fun storeSecretForKey(key: String, secret: String) {
        secrets[key] = secret
    }

    override fun removeSecretForKey(key: String) {
        secrets.remove(key)
    }

    override fun clear() {
        secrets.clear()
    }

    override fun all(): Map<String, String?> {
        return secrets
    }
}