package ch.rethinc.gastrocheckin

import java.util.*

data class Visit(
    val id: UUID,
    val name: String,
    val phone: String,
    val visitedAt: Long
) {
    companion object {
        fun fromPerson(person: Person) =
            Visit(
                id = UUID.randomUUID(),
                name = person.name,
                phone =  person.phone,
                visitedAt = System.currentTimeMillis()
            )
    }
}