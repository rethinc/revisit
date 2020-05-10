package ch.rethinc.gastrocheckin.secretstore

interface SecretsStore {

    fun secretForKey(key: String): String?

    fun storeSecretForKey(key: String, secret: String)

    fun removeSecretForKey(key: String)

    fun clear()

    fun all(): Map<String, String?>
}