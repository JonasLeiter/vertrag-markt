package de.vkb.event

import de.vkb.common.AggregateIdentifier
import de.vkb.models.Vertrag

data class VertragExternalEvent(
    override val id: String,
    override val aggregateIdentifier: AggregateIdentifier,
    override val payload: Vertrag,
    override val type: EventType
) : InternalEvent {
}