package ch.rethinc.gastrocheckin.secretstore

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SecretKeyValidation(
    private val firestore: FirebaseFirestore,
    private val firebaseUser: FirebaseUser,
    private val encryptor: GastroCheckinEncryptor

) {

    companion object {
        private const val challenge = "This is the challenge!"
        private const val challengeField = "challenge"
    }

    fun isValid(secretKey: ByteArray): LiveData<Boolean> =
        Transformations.switchMap(readChallenge()) { storedChallenge ->
            if (storedChallenge == null) {
                saveChallengeEncrypted(secretKey)
            } else {
                val decryptedChallenge = encryptor.decrypt(storedChallenge, secretKey)
                singleValue(decryptedChallenge == challenge)
            }
        }

    fun saveChallengeEncrypted(secretKey: ByteArray): LiveData<Boolean> {
        val encryptedChallenge = encryptor.encrypt(challenge, secretKey)
        if (encryptedChallenge == null) {
            return singleValue(false)
        } else {
            return save(encryptedChallenge)
        }
    }

    private fun singleValue(value: Boolean): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        liveData.postValue(value)
        return liveData
    }

    private fun save(encryptedChallenge: String): LiveData<Boolean> {
        val livedata = MutableLiveData<Boolean>()
        firestore
            .collection("places")
            .document(firebaseUser.uid)
            .set(mapOf(challengeField to encryptedChallenge), SetOptions.merge())
            .addOnSuccessListener {
                livedata.postValue(true)
            }
            .addOnFailureListener { e ->
                Log.e(SecretKeyValidation::class.java.name, "Could not save challenge", e)
                livedata.postValue(false)
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