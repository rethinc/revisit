package ch.rethinc.gastrocheckin

import ch.rethinc.gastrocheckin.AdditionalInformation

interface AdditionalInformationRepository {

    fun get(): AdditionalInformation

    fun save(additionalInformation: AdditionalInformation)
}