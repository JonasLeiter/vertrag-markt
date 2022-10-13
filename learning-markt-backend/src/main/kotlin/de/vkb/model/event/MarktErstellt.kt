package de.vkb.model.event

import java.time.LocalDate

data class MarktErstellt(
    override val commandId: String,
    override val payload: MarktErstelltPayload,
    override val aggregateIdentifier: String,
):Event

data class MarktErstelltPayload(
    val ort: String,
    val datum: LocalDate,
    val vertragId: String
)