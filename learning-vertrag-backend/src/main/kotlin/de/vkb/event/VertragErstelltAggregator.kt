package de.vkb.event

import de.vkb.event.events.*
import de.vkb.event.validation.VertragErstelltValidator
import de.vkb.event.validation.EventValidation
import de.vkb.laser.es.dto.GenericEventAggregatorResult
import de.vkb.laser.es.processor.event.StringKeyCastingEventAggregator
import de.vkb.models.Vertrag
import jakarta.inject.Singleton


// je einen service pro event, auch in readme (sonst dependency injection udn testen problematisch)
// -> validator aufsplitten
// lib arbeitet nach vertical slice prinzip (data class und zugehöriger service gehören zusammen)
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