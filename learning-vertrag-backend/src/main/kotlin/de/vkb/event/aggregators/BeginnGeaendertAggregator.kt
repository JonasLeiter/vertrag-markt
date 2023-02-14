package de.vkb.event.aggregators

import de.vkb.event.events.*
import de.vkb.event.validations.BeginnGeaendertValidator
import de.vkb.event.validations.EventValidation
import de.vkb.laser.es.dto.GenericEventAggregatorResult
import de.vkb.laser.es.processor.event.StringKeyCastingEventAggregator
import de.vkb.models.Vertrag
import jakarta.inject.Singleton

@Singleton
class BeginnGeaendertAggregator(private val validator: BeginnGeaendertValidator)
    :StringKeyCastingEventAggregator<BeginnGeaendert, BeginnGeaendert, Vertrag, EventValidation>
    (BeginnGeaendert::class.java, Vertrag::class.java) {

    override fun processCasted(
        event: BeginnGeaendert,
        aggregate: Vertrag?
    ): GenericEventAggregatorResult<BeginnGeaendert, Vertrag, EventValidation> {
        return validator.validate(event, aggregate)
    }
}