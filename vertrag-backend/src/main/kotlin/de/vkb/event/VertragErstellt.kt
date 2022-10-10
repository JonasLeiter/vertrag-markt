package de.vkb.event

import java.util.Date

data class VertragErstellt(
    override val eventId: String,
    override val aggregateId: String,
    override val payload: VertragErstelltPayload,
) : Event

data class VertragErstelltPayload(
    val vertragId: String,
    val bezeichnung: String,
    val beginn: Date,
    val ende: Date
)
