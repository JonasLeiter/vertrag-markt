package de.vkb.command

import java.util.Date

data class AendereBeginn(
    override val commandId: String,
    override val aggregateId: String,
    override val payload: AendereBeginnPayload,
) : Command

data class AendereBeginnPayload(
    val vertragId: String,
    val beginn: Date
)
