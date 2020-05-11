package ch.rethinc.gastrocheckin

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import ch.rethinc.gastrocheckin.AdditionalInformationRepository

class AdditionalInformationRepositorySharedPreferences(
    private val context: Context
): AdditionalInformationRepository {

    private val sharedPreferences: SharedPreferences
        get() = context.getSharedPreferences(sharedPreferencesName, MODE_PRIVATE)

    companion object {
        private val sharedPreferencesName = "ch.rethinc.gastrocheckin.additionalinformation.waiter"
        private val keyWaiter = "ch.rethinc.gastrocheckin.additionalinformation.waiter"

        private val keyTable = "ch.rethinc.gastrocheckin.additionalinformation.table"
    }

    override fun get(): AdditionalInformation {
        val waiter = sharedPreferences.getString(keyWaiter, "") ?: ""
        val table = sharedPreferences.getString(keyTable, "") ?: ""

        return AdditionalInformation(table, waiter)
    }

    override fun save(additionalInformation: AdditionalInformation) {
        sharedPreferences
            .edit()
            .putString(keyWaiter, additionalInformation.waiter)
            .putString(keyTable, additionalInformation.table)
            .apply()
    }
}