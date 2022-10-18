package de.vkb.streams.event

import de.vkb.model.aggregate.Markt
import de.vkb.model.event.DatumGeandert
import de.vkb.model.event.Event
import de.vkb.model.event.MarktErstellt
import de.vkb.model.event.OrtGeandert
import de.vkb.model.result.EventResult
import de.vkb.model.validation.EventValidation
import jakarta.inject.Singleton

@Singleton
class EventValidator {

    fun validateEvent(event: Event, marktAggregate: Markt?): EventResult {
        var validation = EventValidation(
            commandId = event.commandId,
            isValid = false,
            aggregateIdentifier = event.aggregateIdentifier,
            message = "Unbekannter Event"
        )

        return when (event) {
            is MarktErstellt -> {
                if (marktAggregate == null) {
                    validation = validation.copy(
                        isValid = true,
                        message = "MarktErstellt Event gültig"
                    )
                    val markt = Markt(
                        id = event.aggregateIdentifier,
                        ort = event.payload.ort,
                        datum = event.payload.datum,
                        vertragId = event.payload.vertragId
                    )
                    EventResult(validation, markt, event)
                } else {
                    validation = validation.copy(
                        message = "MarktErstellt Event ungültig - Markt existiert bereits"
                    )
                    EventResult(validation, null, null)
                }
            }

            is OrtGeandert -> {
                if (marktAggregate == null) {
                    validation = validation.copy(
                        message = "OrtGeandert Event ungültig - Markt nicht gefunden"
                    )
                    EventResult(validation, null, null)
                } else {
                    validation = validation.copy(
                        isValid = true,
                        message = "OrtGeandert Event gültig"
                    )
                    val markt = marktAggregate.copy(ort = event.payload.ort)
                    EventResult(validation, markt, event)
                }
            }

            is DatumGeandert -> {
                if (marktAggregate == null) {
                    validation = validation.copy(
                        message = "DatumGeandert Event ungültig - Markt nicht gefunden"
                    )
                    EventResult(validation, null, null)
                } else {
                    validation = validation.copy(
                        isValid = true,
                        message = "DatumGeandert Event gültig"
                    )
                    val markt = marktAggregate.copy(datum = event.payload.datum)
                    EventResult(validation, markt, event)
                }
            }

            else -> {
                EventResult(validation, null, null)
            }
        }
    }
}