package ch.rethinc.store

import ch.rethinc.gastrocheckin.secretstore.Random
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class AesSymmetricCipherTest {

    @Test
    fun encryptAndDecrypt() {
        val outputKeyLength = 256
        val secureRandom = Random.secureRandom()
        val keyGenerator: KeyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(outputKeyLength, secureRandom)
        val key = keyGenerator.generateKey()

        val aesSymmetricCipher = AesSymmetricCipher(SymmetricKeyProviderDummy(key))
        val expectedValue = "mySuperSecretKey"

        val encryptedValue = aesSymmetricCipher.encrypt(expectedValue)
        val decryptedValue = aesSymmetricCipher.decrypt(encryptedValue)

        assertThat(decryptedValue).isEqualTo(expectedValue)
    }

    class SymmetricKeyProviderDummy(
        private val key: SecretKey
    ) : SymmetricKeyProvider {

        override fun getOrCreateSecretKey(): SecretKey {
            return key
        }

        override fun removeKey() {
        }
    }
}