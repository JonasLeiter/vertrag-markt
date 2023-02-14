package de.vkb.streams.event.validator

import de.vkb.laser.es.dto.impl.EventAggregatorResult
import de.vkb.model.aggregate.Markt
import de.vkb.model.event.OrtGeandert
import de.vkb.model.validation.EventValidation
import jakarta.inject.Singleton

@Singleton
class OrtGeandertValidator {

    fun validateOrtGeandert(ortGeandert: OrtGeandert, marktAggregate: Markt?): EventAggregatorResult<OrtGeandert, Markt, EventValidation> {
        return if (marktAggregate == null) {
            EventAggregatorResult(
                feedback = EventValidation(commandId = ortGeandert.commandId, ortGeandert.aggregateIdentifier,
                    "OrtGeandert ungültig - Markt nicht gefunden", hasErrors = true),
                aggregate = null,
                event = null
            )
        } else {
            val markt = marktAggregate.copy(ort = ortGeandert.payload.ort)
            EventAggregatorResult(
                feedback = EventValidation(commandId = ortGeandert.commandId, aggregateIdentifier = ortGeandert.aggregateIdentifier,
                    message = "OrtGeandert gültig", hasErrors = false),
                aggregate = markt,
                event = ortGeandert
            )
        }
    }
}