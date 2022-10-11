package de.vkb.event.events

import java.time.LocalDate

data class BeginnGeandert(
    override var eventId: String,
    override var aggregateId: String,
    override var payload: BeginnGeaendertPayload,
) : Event

data class BeginnGeaendertPayload(
    var vertragId: String,
    var beginn: LocalDate,
)
