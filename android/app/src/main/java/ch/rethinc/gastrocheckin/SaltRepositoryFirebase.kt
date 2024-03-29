package ch.rethinc.gastrocheckin

import android.util.Base64
import android.util.Base64.NO_WRAP
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.rethinc.gastrocheckin.secretstore.Random
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SaltRepositoryFirebase(
    private val firestore: FirebaseFirestore,
    private val firebaseUser: FirebaseUser
) : SaltRepository {

    companion object {
        private const val saltField = "salt"
    }

    private val userDocument: DocumentReference
        get() = firestore
            .collection("places")
            .document(firebaseUser.uid)

    override fun getOrCreateSalt(): LiveData<Result<ByteArray>> {
        val liveData = MutableLiveData<Result<ByteArray>>()
        userDocument
            .get()
            .addOnSuccessListener {
                val saltRaw = it.getSalt()
                if (saltRaw != null) {
                    liveData.postValue(Result.success(Base64.decode(saltRaw, NO_WRAP)))
                } else {
                    val newSalt = generateSalt()
                    userDocument
                        .set(
                            mapOf(saltField to Base64.encodeToString(newSalt, NO_WRAP)),
                            SetOptions.merge()
                        )
                        .addOnSuccessListener {
                            liveData.postValue(Result.success(newSalt))
                        }
                        .addOnFailureListener { e ->
                            Log.e(
                                SaltRepositoryFirebase::class.java.name,
                                "Could not create salt",
                                e
                            )
                            liveData.postValue(Result.failure(e))
                        }
                }

            }
            .addOnFailureListener { e ->
                Log.e(SaltRepositoryFirebase::class.java.name, "Could not get salt", e)
                liveData.postValue(Result.failure(e))
            }

        return liveData
    }

    private fun generateSalt(): ByteArray {
        return Random.bytes(128)
    }

    private fun DocumentSnapshot.getSalt(): String? {
        val data = this.getData() ?: return null
        return data[saltField] as? String
    }
}