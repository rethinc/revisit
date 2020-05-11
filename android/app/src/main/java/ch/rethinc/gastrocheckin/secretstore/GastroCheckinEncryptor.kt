package ch.rethinc.gastrocheckin.secretstore

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import ch.rethinc.gastrocheckin.GastroCheckinKeyStore
import ch.rethinc.gastrocheckin.SaltRepositoryFirebase
import ch.rethinc.store.AesSymmetricCipher
import ch.rethinc.store.SymmetricCipher
import ch.rethinc.store.SymmetricKeyProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class GastroCheckinEncryptor(
    private val gastroCheckinKeyStore: GastroCheckinKeyStore,
    private val keyDerivator: KeyDerivator
) {

    companion object {
        fun createInstance(context: Context, firebaseUser: FirebaseUser): GastroCheckinEncryptor =
            GastroCheckinEncryptor(
                gastroCheckinKeyStore = GastroCheckinKeyStore.getInstance(context),
                keyDerivator = KeyDerivator(SaltRepositoryFirebase(FirebaseFirestore.getInstance(), firebaseUser))
            )
    }

    fun encrypt(cleartext: String): LiveData<Result<String>> {
        val secretKey = gastroCheckinKeyStore.secretKey
            ?: throw IllegalStateException("No secret key defined")
        return encrypt(cleartext, secretKey)
    }

    fun decrypt(input: String): LiveData<Result<String>> {
        val secretKey = gastroCheckinKeyStore.secretKey
            ?: throw IllegalStateException("No secret key defined")
        return decrypt(input, secretKey)
    }

    fun encrypt(cleartext: String, secretKey: String): LiveData<Result<String>> {
        return Transformations.map(keyDerivator.deriveKey(secretKey)) { result ->
            val key = result.getOrNull()
            if (key == null) {
                Result.failure(result.exceptionOrNull() ?: Exception())
            } else {
                Result.success(encrypt(cleartext, key))
            }
        }
    }

    fun decrypt(input: String, secretKey: String): LiveData<Result<String>> {
        return Transformations.map(keyDerivator.deriveKey(secretKey)) { result ->
            val key = result.getOrNull()
            if (key == null) {
                Result.failure(result.exceptionOrNull() ?: Exception())
            } else {
                val cleartext = decrypt(input, key)
                if (cleartext == null) {
                    Result.failure(Exception("Could not decrypt text"))
                } else {
                    Result.success(cleartext)
                }
            }
        }
    }

    private fun createCipher(key: ByteArray): SymmetricCipher {
        val keyProvider = object : SymmetricKeyProvider {
            override fun getOrCreateSecretKey(): SecretKey {
                return SecretKeySpec(key, 0, key.size, "AES")
            }

            override fun removeKey() {
            }
        }

        return AesSymmetricCipher(keyProvider)
    }

    private fun encrypt(cleartext: String, key: ByteArray): String {
        val cipher = createCipher(key)
        return cipher.encrypt(cleartext)
    }

    private fun decrypt(input: String, key: ByteArray): String? {
        try {
            val cipher = createCipher(key)
            return cipher.decrypt(input)
        } catch(t: Throwable) {
            return null
        }
    }
}