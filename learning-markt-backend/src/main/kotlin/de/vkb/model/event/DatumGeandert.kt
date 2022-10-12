package de.vkb.model.event

import java.time.LocalDate

data class DatumGeandert(
    override val commandId: String,
    override val payload: DatumGeandertPayload,
    override val aggregateIdentifier: String,
):Event

data class DatumGeandertPayload(
    val id: String,
    val datum: LocalDate,
)