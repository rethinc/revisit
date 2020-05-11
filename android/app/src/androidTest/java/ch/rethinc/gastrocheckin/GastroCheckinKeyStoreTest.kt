package ch.rethinc.gastrocheckin

import ch.rethinc.gastrocheckin.secretstore.SecretsStore
import ch.rethinc.gastrocheckin.secretstore.SecretsStoreInMemory
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class GastroCheckinKeyStoreTest {

    private lateinit var secretsStore: SecretsStore

    private lateinit var keyStore: GastroCheckinKeyStore

    @Before
    fun setUp() {
        secretsStore = SecretsStoreInMemory()
        keyStore = GastroCheckinKeyStore(secretsStore)
    }

    @Test
    fun storesSecretKey() {
        val key = "myKey".toByteArray()

        keyStore.secretKey = key
        val result = keyStore.secretKey

        assertThat(result).isEqualTo(key)
    }

    @Test
    fun removesSecretKey() {
        keyStore.secretKey = "myKey".toByteArray()

        keyStore.secretKey = null

        val result = keyStore.secretKey
        assertThat(result).isEqualTo(null)
    }
}