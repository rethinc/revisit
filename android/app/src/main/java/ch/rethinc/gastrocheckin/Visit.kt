package ch.rethinc.gastrocheckin

import java.util.*

data class Visit(
    val id: String,
    val name: String,
    val postalCode: String,
    val phone: String,
    val visitedAt: Long,
    val table: String,
    val waiter: String
) {
    companion object {
        fun from(person: Person, additionalInformation: AdditionalInformation) =
            Visit(
                id = UUID.randomUUID().toString(),
                name = person.name,
                phone =  person.phone,
                postalCode = person.postalCode,
                visitedAt = System.currentTimeMillis(),
                table = additionalInformation.table,
                waiter = additionalInformation.waiter
            )
    }
}