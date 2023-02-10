package de.vkb.model.result

import de.vkb.laser.es.dto.GenericEventAggregatorResult
import de.vkb.model.aggregate.Markt
import de.vkb.model.event.Event
import de.vkb.model.validation.EventValidation

data class EventResult(
    override val feedback: EventValidation,
    override val aggregate: Markt?,
    override val event: Event?
): GenericEventAggregatorResult<Event, Markt, EventValidation> {
}