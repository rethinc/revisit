package ch.rethinc.gastrocheckin

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class VisitRepositoryFirebase(
    private val firestore: FirebaseFirestore,
    private val firebaseUser: FirebaseUser
): VisitRepository {

    override fun save(visit: Visit): Task<Void> =
        firestore
            .collection("places")
            .document(firebaseUser.uid)
            .collection("visits")
            .document(visit.id.toString())
            .set(visit)

}