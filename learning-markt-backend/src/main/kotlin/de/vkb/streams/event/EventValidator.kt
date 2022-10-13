package de.vkb.streams.event

import de.vkb.model.aggregate.Markt
import de.vkb.model.aggregate.Vertrag
import de.vkb.model.event.DatumGeandert
import de.vkb.model.event.Event
import de.vkb.model.event.MarktErstellt
import de.vkb.model.event.OrtGeandert
import de.vkb.model.result.EventResult
import de.vkb.model.validation.EventValidation

class EventValidator {

    fun validateEvent(marktAggregate: Markt?, vertragAggregate: Vertrag?, event: Event): EventResult {
        return when (event) {
            is MarktErstellt -> {
                if (marktAggregate == null) {        
                    if(vertragAggregate == null){
                        val validation = EventValidation(
                            commandId = event.commandId,
                            isValid = false,
                            aggregateIdentifier = event.aggregateIdentifier,
                            message = "Markt-Event ungültig - Unbekannter Vertrag"
                        )
                        return EventResult(validation, null)
                    }

                    val validation = EventValidation(
                        commandId = event.commandId,
                        isValid = true,
                        aggregateIdentifier = event.aggregateIdentifier,
                        message = "Markt-Event gültig"
                    )
                    val markt = Markt(
                        id = event.aggregateIdentifier,
                        ort = event.payload.ort,
                        datum = event.payload.datum,
                        vertragId = event.payload.vertragId
                    )
                    EventResult(validation, markt)
                } else {
                    val validation = EventValidation(
                        commandId = event.commandId,
                        isValid = false,
                        aggregateIdentifier = event.aggregateIdentifier,
                        message = "Markt-Event ungültig - Markt existiert bereits"
                    )
                    EventResult(validation, null)
                }
            }

            is OrtGeandert -> {
                if (marktAggregate == null) {
                    val validation = EventValidation(
                        commandId = event.commandId,
                        isValid = false,
                        aggregateIdentifier = event.aggregateIdentifier,
                        message = "Markt-Event ungültig - Markt nicht gefunden"
                    )
                    EventResult(validation, null)
                } else {
                    val validation = EventValidation(
                        commandId = event.commandId,
                        isValid = true,
                        aggregateIdentifier = event.aggregateIdentifier,
                        message = "Markt-Event gültig"
                    )
                    val markt = marktAggregate.copy(ort = event.payload.ort)
                    EventResult(validation, markt)
                }
            }

            is DatumGeandert -> {
                if (marktAggregate == null) {
                    val validation = EventValidation(
                        commandId = event.commandId,
                        isValid = false,
                        aggregateIdentifier = event.aggregateIdentifier,
                        message = "Markt-Event ungültig - Markt nicht gefunden"
                    )
                    EventResult(validation, null)
                } else {
                    val validation = EventValidation(
                        commandId = event.commandId,
                        isValid = true,
                        aggregateIdentifier = event.aggregateIdentifier,
                        message = "Markt-Event gültig"
                    )
                    val markt = marktAggregate.copy(datum = event.payload.datum)
                    EventResult(validation, markt)
                }
            }

            else -> {
                val validation = EventValidation(
                    commandId = event.commandId,
                    isValid = true,
                    aggregateIdentifier = event.aggregateIdentifier,
                    message = "Markt-Event ungültig - Unbekannter Fehler"
                )
                EventResult(validation, null)
            }
        }
    }
}