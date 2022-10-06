package de.vkb.command

import de.vkb.common.AggregateIdentifier
import java.util.Date

data class AendereBeginn(
    override val id: String,
    override val aggregateIdentifier: AggregateIdentifier,
    override val payload: AendereBeginnPayload,
    override val type: CommandType
) : Command

data class AendereBeginnPayload(
    val id: String,
    val beginn: Date
) : Payload
