package de.vkb.streams.event.validator

import de.vkb.laser.es.dto.impl.EventAggregatorResult
import de.vkb.model.aggregate.Markt
import de.vkb.model.event.DatumGeandert
import de.vkb.model.validation.EventValidation
import jakarta.inject.Singleton

@Singleton
class DatumGeandertValidator {
    
    fun validateDatumGeandert(datumGeandert: DatumGeandert, marktAggregate: Markt?): EventAggregatorResult<DatumGeandert, Markt, EventValidation> {
        return if (marktAggregate == null) {
            EventAggregatorResult(
                feedback = EventValidation(commandId = datumGeandert.commandId, datumGeandert.aggregateIdentifier,
                    "DatumGeandert ungültig - Markt nicht gefunden", hasErrors = true),
                aggregate = null,
                event = null
            )
        } else {
            val markt = marktAggregate.copy(datum = datumGeandert.payload.datum)
            EventAggregatorResult(
                feedback = EventValidation(commandId = datumGeandert.commandId, aggregateIdentifier = datumGeandert.aggregateIdentifier,
                    message = "DatumGeandert gültig", hasErrors = false),
                aggregate = markt,
                event = datumGeandert
            )
        }
    }
}