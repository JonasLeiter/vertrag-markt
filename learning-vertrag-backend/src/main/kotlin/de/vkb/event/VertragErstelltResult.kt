package de.vkb.event

import de.vkb.event.events.Event
import de.vkb.event.validation.EventValidation
import de.vkb.laser.es.dto.GenericEventAggregatorResult
import de.vkb.models.Vertrag

data class EventResult(
    override val event: Event?,
    override val aggregate: Vertrag?,
    override val feedback: EventValidation
) : GenericEventAggregatorResult<Event, Vertrag, EventValidation>
