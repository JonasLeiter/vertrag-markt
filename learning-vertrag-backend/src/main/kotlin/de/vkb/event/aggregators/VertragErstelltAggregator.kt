package de.vkb.event.aggregators

import de.vkb.event.events.*
import de.vkb.event.validations.VertragErstelltValidator
import de.vkb.event.validations.EventValidation
import de.vkb.laser.es.dto.GenericEventAggregatorResult
import de.vkb.laser.es.processor.event.StringKeyCastingEventAggregator
import de.vkb.models.Vertrag
import jakarta.inject.Singleton

@Singleton
class VertragErstelltAggregator(private val validator: VertragErstelltValidator)
    :StringKeyCastingEventAggregator<VertragErstellt, VertragErstellt, Vertrag, EventValidation>
    (VertragErstellt::class.java, Vertrag::class.java) {

    override fun processCasted(
        event: VertragErstellt,
        aggregate: Vertrag?
    ): GenericEventAggregatorResult<VertragErstellt, Vertrag, EventValidation> {
        return validator.validate(event, aggregate)
    }
}