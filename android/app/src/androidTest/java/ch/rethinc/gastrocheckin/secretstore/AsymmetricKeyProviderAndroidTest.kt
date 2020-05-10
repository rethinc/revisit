package ch.rethinc.store

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import ch.rethinc.store.AsymmetricKeyProviderAndroid.Algorithm.RSA
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.security.KeyStore

class AsymmetricKeyProviderAndroidTest {

    companion object {
        private const val keyAlias = "keyAlias"
    }

    private lateinit var asymmetricKeyProvider: AsymmetricKeyProvider

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        this.asymmetricKeyProvider = AsymmetricKeyProviderAndroid(
            keyType = RSA,
            keyAlias = keyAlias,
            context = context
        )
    }

    @After
    fun removeEntry() {
        val keyStore = androidKeyStore()
        keyStore.deleteEntry(keyAlias)
    }

    @Test
    fun privateKeyCreatesKeyPairInAndroidKeyStore() {
        val keyPair = asymmetricKeyProvider.getOrCreateKeyPair()

        assertThat(keyPair).isNotNull()
        val entry = androidKeyStore().getEntry(keyAlias, null) as KeyStore.PrivateKeyEntry
        assertThat(entry.privateKey).isEqualTo(keyPair.private)
    }

    @Test
    fun publicKeyCreatesKeyPairInAndroidKeyStore() {
        val keyPair = asymmetricKeyProvider.getOrCreateKeyPair()

        assertThat(keyPair).isNotNull()
        val entry = androidKeyStore().getEntry(keyAlias, null) as KeyStore.PrivateKeyEntry
        assertThat(entry.certificate.publicKey).isEqualTo(keyPair.public)
    }

    @Test
    fun removeKeypair() {
        asymmetricKeyProvider.getOrCreateKeyPair()

        asymmetricKeyProvider.removeKeyPair()

        val entry = androidKeyStore().getEntry(keyAlias, null)
        assertThat(entry).isNull()
    }

    @Test
    fun generateKeypairMultipleTimesIsIdempotent() {
        val firstKeyPair = asymmetricKeyProvider.getOrCreateKeyPair()

        val secondKeyPair = asymmetricKeyProvider.getOrCreateKeyPair()

        assertThat(firstKeyPair.public).isEqualTo(secondKeyPair.public)
        assertThat(firstKeyPair.private).isEqualTo(secondKeyPair.private)
    }


    private fun androidKeyStore(): KeyStore {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore
    }
}