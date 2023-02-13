package de.vkb.streams.event.aggregator

import de.vkb.laser.es.dto.GenericEventAggregatorResult
import de.vkb.laser.es.processor.event.StringKeyCastingEventAggregator
import de.vkb.model.aggregate.Markt
import de.vkb.model.event.OrtGeandert
import de.vkb.model.validation.EventValidation
import de.vkb.streams.event.validator.OrtGeandertValidator
import jakarta.inject.Singleton

@Singleton
class OrtGeandertAggregator(private val validator: OrtGeandertValidator): StringKeyCastingEventAggregator<OrtGeandert, OrtGeandert, Markt, EventValidation>(
    OrtGeandert::class.java,
    Markt::class.java,
) {

    override fun processCasted(
        event: OrtGeandert,
        aggregate: Markt?
    ): GenericEventAggregatorResult<OrtGeandert, Markt, EventValidation> {
        return validator.validateOrtGeandert(event, aggregate)
    }
}