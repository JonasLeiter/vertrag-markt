package de.vkb.event.events

import java.time.LocalDate

data class BeginnGeaendert(
    override var commandId: String,
    override var aggregateId: String,
    override var payload: BeginnGeaendertPayload,
) : Event

data class BeginnGeaendertPayload(
    var vertragId: String,
    var beginn: LocalDate,
)
