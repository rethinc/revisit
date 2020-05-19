package ch.rethinc.gastrocheckin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object LiveDatas {

    fun <T> singleValue(value: T): LiveData<T> {
        val liveData = MutableLiveData<T>()
        liveData.postValue(value)
        return liveData
    }
}