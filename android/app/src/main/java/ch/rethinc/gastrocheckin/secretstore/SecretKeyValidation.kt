package ch.rethinc.gastrocheckin.secretstore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SecretKeyValidation(
    private val firestore: FirebaseFirestore,
    private val firebaseUser: FirebaseUser,
    private val gastroCheckinEncryptor: GastroCheckinEncryptor

) {

    companion object {
        private const val challenge = "This is the challenge!"
        private const val challengeField = "challenge"
    }

    fun isValid(secretKey: String): LiveData<Boolean> =
        Transformations.switchMap(readChallenge()) { storedChallenge ->
            if (storedChallenge == null) {
                Transformations.map(saveChallengeEncrypted(secretKey)) { result ->
                    result.isSuccess
                }
            } else {
                Transformations.map(gastroCheckinEncryptor.decrypt(storedChallenge, secretKey)) { result ->
                    val decryptedChallenge = result.getOrNull()
                    decryptedChallenge == challenge
                }
            }
        }

    fun saveChallengeEncrypted(secretKey: String): LiveData<Result<Unit>> =
        Transformations.switchMap(gastroCheckinEncryptor.encrypt(challenge, secretKey)) { result ->
            val encryptedChallenge = result.getOrNull()
            if (encryptedChallenge == null) {
                val liveData = MutableLiveData<Result<Unit>>()
                liveData.postValue(Result.failure(result.exceptionOrNull() ?: Exception()))
                liveData
            } else {
                save(encryptedChallenge)
            }
        }

    private fun save(encryptedChallenge: String): LiveData<Result<Unit>> {
        val livedata = MutableLiveData<Result<Unit>>()
        firestore
            .collection("places")
            .document(firebaseUser.uid)
            .set(mapOf(challengeField to encryptedChallenge), SetOptions.merge())
            .addOnSuccessListener {
                livedata.postValue(Result.success(Unit))
            }
            .addOnFailureListener { e ->
                livedata.postValue(Result.failure(e))
            }
        return livedata
    }

    fun readChallenge(): LiveData<String?> {
        val liveData = MutableLiveData<String>()
        firestore
            .collection("places")
            .document(firebaseUser.uid)
            .get()
            .addOnSuccessListener { doc ->
                liveData.postValue(doc[challengeField] as? String)
            }.addOnFailureListener {
                liveData.postValue(null)
            }
        return liveData
    }
}