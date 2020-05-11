package ch.rethinc.gastrocheckin

import androidx.lifecycle.LiveData

interface VisitRepository {

    fun save(visit: Visit): LiveData<Result<Unit>>
}