package de.vkb.event

import java.util.Date

data class EndeGeandert(
    override val eventId: String,
    override val aggregateId: String,
    override val payload: EndeGeaendertPayload,
) : Event

data class EndeGeaendertPayload(
    val vertragId: String,
    val ende: Date,
)
