package ch.rethinc.store

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import ch.rethinc.store.AsymmetricKeyProviderAndroid.Algorithm.RSA
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.security.KeyStore

class RsaAsymmetricEncrypterTest {

    companion object {
        private const val keyAlias = "keyAlias"
    }

    private lateinit var cipher: AsymmetricCipher

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        cipher = RsaAsymmetricCipher(AsymmetricKeyProviderAndroid(
            keyType = RSA,
            keyAlias = keyAlias,
            context = context
        ))
    }

    @After
    fun removeEntry() {
        val keyStore = keyStore()
        keyStore.deleteEntry(keyAlias)
    }

    @Test
    fun removeKeypair() {
        cipher.encrypt("test".toByteArray())

        cipher.removeKeyPair()

        val entry = keyStore().getEntry(keyAlias, null)
        assertThat(entry).isNull()
    }

    @Test
    fun encryptDecrypt() {
        val expectedString = "mySuperSecretValue"

        val encrypted = cipher.encrypt(expectedString.toByteArray())
        val decrypted = cipher.decrypt(encrypted!!)!!

        val decryptedString = String(decrypted)
        assertThat(decryptedString).isEqualTo(expectedString)
    }

    private fun keyStore(): KeyStore {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore
    }
}