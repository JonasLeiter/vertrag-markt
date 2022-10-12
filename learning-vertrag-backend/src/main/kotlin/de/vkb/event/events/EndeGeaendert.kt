package de.vkb.event.events

import java.time.LocalDate

data class EndeGeaendert(
    override var eventId: String,
    override var aggregateId: String,
    override var payload: EndeGeaendertPayload,
) : Event

data class EndeGeaendertPayload(
    var vertragId: String,
    var ende: LocalDate,
)
