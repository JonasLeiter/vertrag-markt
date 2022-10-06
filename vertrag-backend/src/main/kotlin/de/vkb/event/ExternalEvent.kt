package de.vkb.event

import de.vkb.common.AggregateIdentifier
import de.vkb.models.Vertrag

interface ExternalEvent {
    val id: String
    val aggregateIdentifier: AggregateIdentifier
    val payload: Vertrag
    val type: EventType
}