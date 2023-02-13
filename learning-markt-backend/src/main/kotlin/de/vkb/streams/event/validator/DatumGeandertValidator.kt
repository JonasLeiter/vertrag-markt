package de.vkb.streams.event.validator

import de.vkb.model.aggregate.Markt
import de.vkb.model.event.DatumGeandert
import de.vkb.model.result.event.DatumGeandertResult
import de.vkb.model.validation.EventValidation
import jakarta.inject.Singleton

@Singleton
class DatumGeandertValidator {
    
    fun validateDatumGeandert(datumGeandert: DatumGeandert, marktAggregate: Markt?): DatumGeandertResult {
        return if (marktAggregate == null) {
            DatumGeandertResult(
                feedback = EventValidation(commandId = datumGeandert.commandId, datumGeandert.aggregateIdentifier,
                    "DatumGeandert ungültig - Markt nicht gefunden", hasErrors = true),
                aggregate = null,
                event = null
            )
        } else {
            val markt = marktAggregate.copy(datum = datumGeandert.payload.datum)
            DatumGeandertResult(
                feedback = EventValidation(commandId = datumGeandert.commandId, aggregateIdentifier = datumGeandert.aggregateIdentifier,
                    message = "DatumGeandert gültig", hasErrors = false),
                aggregate = markt,
                event = datumGeandert
            )
        }
    }
}