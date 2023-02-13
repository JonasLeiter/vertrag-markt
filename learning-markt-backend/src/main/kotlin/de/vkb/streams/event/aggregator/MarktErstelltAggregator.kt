package de.vkb.streams.event.aggregator

import de.vkb.laser.es.dto.GenericEventAggregatorResult
import de.vkb.laser.es.processor.event.StringKeyCastingEventAggregator
import de.vkb.model.aggregate.Markt
import de.vkb.model.event.MarktErstellt
import de.vkb.model.validation.EventValidation
import de.vkb.streams.event.validator.MarktErstelltValidator
import jakarta.inject.Singleton

@Singleton
class MarktErstelltAggregator(private val validator: MarktErstelltValidator): StringKeyCastingEventAggregator<MarktErstellt, MarktErstellt, Markt, EventValidation>(
    MarktErstellt::class.java,
    Markt::class.java,
) {

    override fun processCasted(
        event: MarktErstellt,
        aggregate: Markt?
    ): GenericEventAggregatorResult<MarktErstellt, Markt, EventValidation> {
        return validator.validateMarktErstellt(event, aggregate)
    }


}