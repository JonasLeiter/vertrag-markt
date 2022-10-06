package de.vkb.command

import de.vkb.common.AggregateIdentifier
import de.vkb.models.Vertrag

interface Command {
    val id: String
    val aggregateIdentifier: AggregateIdentifier
    val payload: Vertrag
    val type: CommandType
}