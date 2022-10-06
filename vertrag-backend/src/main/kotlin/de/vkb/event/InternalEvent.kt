package de.vkb.event

import de.vkb.command.CommandType
import de.vkb.common.AggregateIdentifier
import de.vkb.models.Vertrag

interface InternalEvent {
    val id: String
    val aggregateIdentifier: AggregateIdentifier
    val payload: Vertrag
    val type: EventType
}