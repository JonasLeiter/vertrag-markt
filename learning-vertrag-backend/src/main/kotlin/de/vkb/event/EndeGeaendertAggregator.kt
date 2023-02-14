package de.vkb.event

import de.vkb.event.events.*
import de.vkb.event.validation.EndeGeaendertValidator
import de.vkb.event.validation.EventValidation
import de.vkb.laser.es.dto.GenericEventAggregatorResult
import de.vkb.laser.es.processor.event.StringKeyCastingEventAggregator
import de.vkb.models.Vertrag
import jakarta.inject.Singleton


// je einen service pro event, auch in readme (sonst dependency injection udn testen problematisch)
// -> validator aufsplitten
// lib arbeitet nach vertical slice prinzip (data class und zugehöriger service gehören zusammen)
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