package ch.rethinc.gastrocheckin.secretstore

import android.app.Application
import androidx.lifecycle.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import ch.rethinc.gastrocheckin.LiveDatas
import ch.rethinc.gastrocheckin.SaltRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.MILLISECONDS

class GastroCheckinEncryptorTest {

    private lateinit var encryptor: GastroCheckinEncryptor

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        encryptor = GastroCheckinEncryptor.createInstance(context)
    }

    @Test
    fun encrypt() {
        UiThreadStatement.runOnUiThread {
            val cleartext = "This is cleartext"
            val key = anyKey()

            val ciphertext = encryptor.encrypt(cleartext, key)!!
            val decrypted = encryptor.decrypt(ciphertext, key)

            assertThat(decrypted).isEqualTo(cleartext)
        }
    }

    private fun anyKey(): ByteArray {
        var key = byteArrayOf()
        val countDownLatch = CountDownLatch(1)
        val keyDerivator = KeyDerivator(saltRepository = SaltRepositoryFixed())
        keyDerivator.deriveKey(userPassword = "test")
            .observe(TestLifecycleOwner(), Observer { result ->
                key = result.getOrThrow()
                countDownLatch.countDown()
            })
        val success = countDownLatch.await(100, MILLISECONDS)
        if (!success) {
            throw IllegalStateException("Could not get key")
        }

        return key
    }

    class TestLifecycleOwner : LifecycleOwner {

        override fun getLifecycle(): Lifecycle {
            val lifecycleRegistry = LifecycleRegistry(this)
            return lifecycleRegistry
        }
    }

    class SaltRepositoryFixed : SaltRepository {

        override fun getOrCreateSalt(): LiveData<Result<ByteArray>> =
            LiveDatas.singleValue(
                Result.success(ByteArray(16) { it.toByte() })
            )
    }

}