package de.vkb.streams.event

import de.vkb.laser.es.dto.GenericEventAggregatorResult
import de.vkb.laser.es.processor.event.StringKeyCastingEventAggregator
import de.vkb.model.aggregate.Markt
import de.vkb.model.event.Event
import de.vkb.model.validation.EventValidation
import jakarta.inject.Singleton

@Singleton
class EventAggregator(private val validator: EventValidator): StringKeyCastingEventAggregator<Event, Event, Markt, EventValidation>(
    Event::class.java,
    Markt::class.java,
) {
    override fun processCasted(
        event: Event,
        aggregate: Markt?
    ): GenericEventAggregatorResult<Event, Markt, EventValidation> {
        return validator.validateEvent(event, aggregate)
    }


}