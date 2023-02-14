package de.vkb.event.aggregators

import de.vkb.event.events.*
import de.vkb.event.validations.EndeGeaendertValidator
import de.vkb.event.validations.EventValidation
import de.vkb.laser.es.dto.GenericEventAggregatorResult
import de.vkb.laser.es.processor.event.StringKeyCastingEventAggregator
import de.vkb.models.Vertrag
import jakarta.inject.Singleton

@Singleton
class EndeGeaendertAggregator(private val validator: EndeGeaendertValidator)
    :StringKeyCastingEventAggregator<EndeGeaendert, EndeGeaendert, Vertrag, EventValidation>
    (EndeGeaendert::class.java, Vertrag::class.java) {

    override fun processCasted(
        event: EndeGeaendert,
        aggregate: Vertrag?
    ): GenericEventAggregatorResult<EndeGeaendert, Vertrag, EventValidation> {
        return validator.validate(event, aggregate)
    }
}