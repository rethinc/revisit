package ch.rethinc.gastrocheckin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.rethinc.gastrocheckin.secretstore.GastroCheckinEncryptor
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class VisitRepositoryFirebase(
    private val firestore: FirebaseFirestore,
    private val firebaseUser: FirebaseUser,
    private val encryptor: GastroCheckinEncryptor
) : VisitRepository {

    override fun save(visit: Visit): LiveData<Result<Unit>> =
        storeInFirebase(encrypt(visit))

    private fun storeInFirebase(visit: Visit): LiveData<Result<Unit>> {
        val liveData = MutableLiveData<Result<Unit>>()
        firestore
            .collection("places")
            .document(firebaseUser.uid)
            .collection("visits")
            .document(visit.id.toString())
            .set(visit)
            .addOnSuccessListener {
                liveData.postValue(Result.success(Unit))
            }.addOnFailureListener { exception ->
                liveData.postValue(Result.failure(exception))
            }
        return liveData
    }


    private fun encrypt(visit: Visit): Visit =
        visit.copy(
            name = encryptor.encrypt(visit.name) ?: "",
            phone = encryptor.encrypt(visit.phone) ?: "",
            table = encryptor.encrypt(visit.table) ?: "",
            waiter = encryptor.encrypt(visit.waiter) ?: ""
        )

}