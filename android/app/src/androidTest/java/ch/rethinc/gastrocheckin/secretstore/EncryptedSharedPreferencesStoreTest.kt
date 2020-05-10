package ch.rethinc.store

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import ch.rethinc.gastrocheckin.secretstore.SecretEncryptionException
import ch.rethinc.gastrocheckin.secretstore.SecretsStore
import ch.rethinc.store.AsymmetricKeyProviderAndroid.Algorithm.RSA
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Before
import org.junit.Test

class EncryptedSharedPreferencesStoreTest {

    companion object {
        private const val keyAlias = "symmetricKeyAlias"
        private const val symmetricKeyStoreName = "ch.viavelo.customerapp.symmetric"
        private const val sharedPreferenceStoreName = "ch.viavelo.customerapp.asymmetric"
    }

    private lateinit var symmetricKeyProvider: SymmetricKeyProvider

    private lateinit var asymmetricKeyProvider: AsymmetricKeyProvider

    private lateinit var secretsStore: SecretsStore

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        asymmetricKeyProvider = asymmetricKeyProvider(context)
        symmetricKeyProvider = symmetricKeyProvider(context, asymmetricKeyProvider)
        secretsStore = encryptedSecretsStore(context, symmetricKeyProvider)
        secretsStore.clear()
    }

    @Test
    fun storeSecret() {
        val key = "key"
        val secret = "secret"
        val store = encryptedSecretsStore()

        store.storeSecretForKey(key, secret)
        val result = store.secretForKey(key)

        assertThat(result).isEqualTo(secret)
    }

    @Test
    fun nonExistingSecret() {
        val result = secretsStore.secretForKey("key")

        assertThat(result).isNull()
    }

    @Test
    fun removeSecret() {
        val key = "key"
        val secret = "secret"
        secretsStore.storeSecretForKey(key, secret)

        secretsStore.removeSecretForKey(key)

        val result = secretsStore.secretForKey(key)
        assertThat(result).isNull()
    }

    @Test
    fun clear() {
        val key = "key"
        val secret = "secret"
        secretsStore.storeSecretForKey(key, secret)

        secretsStore.clear()

        val result = secretsStore.secretForKey(key)
        assertThat(result).isNull()
    }

    @Test
    fun clearAlsoClearsSecretKeys() {
        secretsStore.storeSecretForKey("key", "secret")
        val originalSymmetricKey = symmetricKeyProvider.getOrCreateSecretKey()
        val originalAsymmetricKey = asymmetricKeyProvider.getOrCreateKeyPair()

        secretsStore.clear()

        assertThat(symmetricKeyProvider.getOrCreateSecretKey()).isNotEqualTo(originalSymmetricKey)
        assertThat(asymmetricKeyProvider.getOrCreateKeyPair()).isNotEqualTo(originalAsymmetricKey)
    }

    @Test
    fun throwsExceptionIfSymmetricKeyHasBeenDeleted() {
        val key = "key"
        secretsStore.storeSecretForKey(key, "secret")

        symmetricKeyProvider.removeKey()

        assertThatThrownBy {
            secretsStore.secretForKey(key)
        }.isInstanceOf(SecretEncryptionException::class.java)
    }

    @Test
    fun throwsExceptionIfSymmetricKeyHasChanged() {
        val key = "key"
        secretsStore.storeSecretForKey(key, "secret")

        symmetricKeyProvider.removeKey()
        symmetricKeyProvider.getOrCreateSecretKey()

        assertThatThrownBy {
            secretsStore.secretForKey(key)
        }.isInstanceOf(SecretEncryptionException::class.java)
    }

    @Test
    fun throwsExceptionIfAsymmetricKeyHasBeenDeleted() {
        val key = "key"
        secretsStore.storeSecretForKey(key, "secret")

        asymmetricKeyProvider.removeKeyPair()

        assertThatThrownBy {
            secretsStore.secretForKey(key)
        }.isInstanceOf(SecretEncryptionException::class.java)
    }

    @Test
    fun throwsExceptionIfASymmetricKeyHasChanged() {
        val key = "key"
        secretsStore.storeSecretForKey(key, "secret")

        asymmetricKeyProvider.removeKeyPair()
        asymmetricKeyProvider.getOrCreateKeyPair()

        assertThatThrownBy {
            secretsStore.secretForKey(key)
        }.isInstanceOf(SecretEncryptionException::class.java)
    }

    @Test
    fun reinstantiateKeepsKeys() {
        val key = "key"
        val expectedSecret = "SuperSecretValue"
        val firstInstance = encryptedSecretsStore()
        firstInstance.storeSecretForKey(key, expectedSecret)
        val secondInstance = encryptedSecretsStore()

        val secret = secondInstance.secretForKey(key)

        assertThat(secret).isEqualTo(expectedSecret)
    }

    private fun encryptedSecretsStore(
        context: Context = ApplicationProvider.getApplicationContext<Application>(),
        symmetricKeyProvider: SymmetricKeyProvider = symmetricKeyProvider(context)
    ): SecretsStore {
        return EncryptedSharedPreferencesStore(
            context = context,
            symmetricCipher = AesSymmetricCipher(symmetricKeyProvider),
            sharedPreferencesStoreName = sharedPreferenceStoreName
        )
    }

    private fun symmetricKeyProvider(
        context: Context,
        asymmetricKeyProvider: AsymmetricKeyProvider = asymmetricKeyProvider(context)
    ) =
        SymmetricKeyProviderAndroid(
            asymmetricCipher = RsaAsymmetricCipher(asymmetricKeyProvider),
            keyStoreName = symmetricKeyStoreName,
            context = context
        )

    private fun asymmetricKeyProvider(context: Context): AsymmetricKeyProvider =
        AsymmetricKeyProviderAndroid(
            keyType = RSA,
            keyAlias = keyAlias,
            context = context
        )

}