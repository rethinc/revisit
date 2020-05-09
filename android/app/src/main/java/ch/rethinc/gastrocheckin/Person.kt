package ch.rethinc.gastrocheckin

import com.google.gson.Gson

data class Person(
    val name: String,
    val phone: String
) {
    companion object {
        fun fromJson(json: String): Person? {
            try {
                return Gson().fromJson(json, Person::class.java)
            } catch(_: Throwable) {
                return null
            }
        }
    }
}