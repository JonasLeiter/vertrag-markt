package de.vkb.command

import de.vkb.common.AggregateIdentifier

interface Command {
    val id: String
    val aggregateIdentifier: AggregateIdentifier
    val payload: Payload
    val type: CommandType
}