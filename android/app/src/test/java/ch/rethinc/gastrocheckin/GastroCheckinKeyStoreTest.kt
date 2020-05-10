package ch.rethinc.gastrocheckin

import ch.rethinc.gastrocheckin.secretstore.SecretsStore
import ch.rethinc.gastrocheckin.secretstore.SecretsStoreInMemory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GastroCheckinKeyStoreTest {

    private lateinit var secretsStore: SecretsStore

    private lateinit var keyStore: GastroCheckinKeyStore

    @BeforeEach
    fun setUp() {
        secretsStore = SecretsStoreInMemory()
        keyStore = GastroCheckinKeyStore(secretsStore)
    }

    @Test
    fun storesSecretKey() {
        val key = "myKey"

        keyStore.secretKey = key
        val result = keyStore.secretKey

        assertThat(result).isEqualTo(key)
    }

    @Test
    fun removesSecretKey() {
        val key = "myKey"
        keyStore.secretKey = "myKey"

        keyStore.secretKey = null

        val result = keyStore.secretKey
        assertThat(result).isEqualTo(null)
    }
}