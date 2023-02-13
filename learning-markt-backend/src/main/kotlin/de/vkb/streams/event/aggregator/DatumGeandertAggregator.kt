package de.vkb.streams.event.aggregator

import de.vkb.laser.es.dto.GenericEventAggregatorResult
import de.vkb.laser.es.processor.event.StringKeyCastingEventAggregator
import de.vkb.model.aggregate.Markt
import de.vkb.model.event.DatumGeandert
import de.vkb.model.validation.EventValidation
import de.vkb.streams.event.validator.DatumGeandertValidator
import jakarta.inject.Singleton

@Singleton
class DatumGeandertAggregator(private val validator: DatumGeandertValidator): StringKeyCastingEventAggregator<DatumGeandert, DatumGeandert, Markt, EventValidation>(
    DatumGeandert::class.java,
    Markt::class.java,
) {

    override fun processCasted(
        event: DatumGeandert,
        aggregate: Markt?
    ): GenericEventAggregatorResult<DatumGeandert, Markt, EventValidation> {
        return validator.validateDatumGeandert(event, aggregate)
    }
}