package de.vkb.event

import java.util.Date

data class BeginnGeandert(
    override val eventId: String,
    override val aggregateId: String,
    override val payload: BeginnGeaendertPayload,
) : InternalEvent

data class BeginnGeaendertPayload(
    val vertragId: String,
    val beginn: Date,
)
