package ch.rethinc.gastrocheckin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import ch.rethinc.gastrocheckin.secretstore.GastroCheckinEncryptor
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class VisitRepositoryFirebase(
    private val firestore: FirebaseFirestore,
    private val firebaseUser: FirebaseUser,
    private val encryptor: GastroCheckinEncryptor
) : VisitRepository {

    override fun save(visit: Visit): LiveData<Result<Unit>> =
        Transformations.switchMap(encrypt(visit)) { encryptedVisit ->
            storeInFirebase(encryptedVisit)
        }

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


    private fun encrypt(visit: Visit): LiveData<Visit> =
        Transformations.switchMap(encryptor.encrypt(visit.name)) { encryptedNameResult ->
            Transformations.switchMap(encryptor.encrypt(visit.phone)) { encryptedPhoneResult ->
                Transformations.switchMap(encryptor.encrypt(visit.table)) { encryptedTableResult ->
                    Transformations.map(encryptor.encrypt(visit.waiter)) { encryptedWaiterResult ->
                        visit.copy(
                            name = encryptedNameResult.getOrThrow(),
                            phone = encryptedPhoneResult.getOrThrow(),
                            table = encryptedTableResult.getOrThrow(),
                            waiter = encryptedWaiterResult.getOrThrow()
                        )
                    }
                }
            }
        }

}