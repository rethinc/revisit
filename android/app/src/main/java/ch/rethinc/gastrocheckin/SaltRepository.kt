package ch.rethinc.gastrocheckin

import androidx.lifecycle.LiveData

interface SaltRepository {

    fun getOrCreateSalt(): LiveData<Result<ByteArray>>
}