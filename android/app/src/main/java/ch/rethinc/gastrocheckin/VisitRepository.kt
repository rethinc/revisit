package ch.rethinc.gastrocheckin

import com.google.android.gms.tasks.Task

interface VisitRepository {

    fun save(visit: Visit): Task<Void>
}