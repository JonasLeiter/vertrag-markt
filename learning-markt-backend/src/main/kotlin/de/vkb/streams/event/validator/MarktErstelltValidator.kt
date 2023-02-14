package de.vkb.streams.event.validator

import de.vkb.laser.es.dto.impl.EventAggregatorResult
import de.vkb.model.aggregate.Markt
import de.vkb.model.event.MarktErstellt
import de.vkb.model.validation.EventValidation
import jakarta.inject.Singleton

@Singleton
class MarktErstelltValidator {
    
    fun validateMarktErstellt(marktErstellt: MarktErstellt, marktAggregate: Markt?): EventAggregatorResult<MarktErstellt, Markt, EventValidation> {
        return if (marktAggregate == null) {
            val markt = Markt(
                id = marktErstellt.aggregateIdentifier,
                ort = marktErstellt.payload.ort,
                datum = marktErstellt.payload.datum,
                vertragId = marktErstellt.payload.vertragId
            )

            EventAggregatorResult(
                feedback = EventValidation(commandId = marktErstellt.commandId, marktErstellt.aggregateIdentifier,
                    "MarktErstellt gültig", hasErrors = false),
                aggregate = markt,
                event = marktErstellt
            )
        } else {
            EventAggregatorResult(
                feedback = EventValidation(commandId = marktErstellt.commandId, aggregateIdentifier = marktErstellt.aggregateIdentifier,
                    message = "MarktErstellt ungültig - Markt existiert bereits", hasErrors = true),
                aggregate = null,
                event = null
            )
        }
    }
}