package de.vkb.command

import de.vkb.common.AggregateIdentifier
import de.vkb.models.Vertrag
import java.util.Date

data class ErstelleVertrag(
    override val id: String,
    override val aggregateIdentifier: AggregateIdentifier,
    override val payload: ErstelleVertragPayload,
    override val type: CommandType
) : Command

data class ErstelleVertragPayload(
    val bezeichnung: String,
    val beginn: Date,
    val ende: Date
) : Payload
