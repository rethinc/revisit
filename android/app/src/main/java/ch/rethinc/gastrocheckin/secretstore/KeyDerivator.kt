package ch.rethinc.gastrocheckin.secretstore

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import ch.rethinc.gastrocheckin.SaltRepository
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class KeyDerivator(
    private val saltRepository: SaltRepository
) {

    fun deriveKey(userPassword: String): LiveData<Result<ByteArray>> =
        Transformations.map(
            saltRepository.getOrCreateSalt()
        ) { result ->
            val salt = result.getOrNull()
            if (salt == null) {
                Result.failure(result.exceptionOrNull() ?: Exception())
            } else {
                Result.success(deriveKey(userPassword, salt))
            }
        }

    private fun deriveKey(userPassword: String, salt: ByteArray): ByteArray {
        val iterationCount = 12000
        val keyLength = 32
        val keyspec =
            PBEKeySpec(userPassword.toCharArray(), salt, iterationCount, keyLength * 8)

        val factory = SecretKeyFactory.getInstance("PBKDF2WithhmacSHA256")
        return factory.generateSecret(keyspec).encoded
    }
}