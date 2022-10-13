package de.vkb.event.events

import java.time.LocalDate

data class VertragErstellt(
    override var commandId: String,
    override var aggregateId: String,
    override var payload: VertragErstelltPayload,
) : Event

data class VertragErstelltPayload(
    var vertragId: String,
    var bezeichnung: String,
    var beginn: LocalDate,
    var ende: LocalDate
)
