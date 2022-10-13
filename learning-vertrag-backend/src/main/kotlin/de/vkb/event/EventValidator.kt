package de.vkb.event

import de.vkb.event.events.BeginnGeaendert
import de.vkb.event.events.EndeGeaendert
import de.vkb.event.events.Event
import de.vkb.event.events.VertragErstellt
import de.vkb.common.ValidationType
import de.vkb.models.Vertrag
import jakarta.inject.Singleton

@Singleton
class EventValidator {

    fun validate(event: Event?, vertrag: Vertrag?): EventValidationResult =
        when (event) {
            is VertragErstellt -> {
                if (vertrag == null) {
                    EventValidationResult(
                        commandId = event.commandId,
                        aggregateId = event.aggregateId,
                        valid = true,
                        validationType = ValidationType.Gueltig,
                        exception = ""
                    )
                } else {
                    EventValidationResult(
                        commandId = event.commandId,
                        aggregateId = event.aggregateId,
                        valid = false,
                        validationType = ValidationType.Ungueltig,
                        exception = "Vertrag existiert bereits"
                    )
                }
            }

            is BeginnGeaendert -> {
                if (vertrag == null) {
                    EventValidationResult(
                        commandId = event.commandId,
                        aggregateId = event.aggregateId,
                        valid = false,
                        validationType = ValidationType.Ungueltig,
                        exception = "Vertrag existiert nicht"
                    )
                } else if (event.payload.beginn.isAfter(vertrag.ende)) {
                    EventValidationResult(
                        commandId = event.commandId,
                        aggregateId = event.aggregateId,
                        valid = false,
                        validationType = ValidationType.Ungueltig,
                        exception = "Neuer Beginn liegt vor Vertrags-Ende"
                    )
                } else {
                    EventValidationResult(
                        commandId = event.commandId,
                        aggregateId = event.aggregateId,
                        valid = true,
                        validationType = ValidationType.Gueltig,
                        exception = ""
                    )
                }
            }

            is EndeGeaendert -> {
                if (vertrag == null) {
                    EventValidationResult(
                        commandId = event.commandId,
                        aggregateId = event.aggregateId,
                        valid = false,
                        validationType = ValidationType.Ungueltig,
                        exception = "Vertrag existiert nicht"
                    )
                } else if (event.payload.ende.isBefore(vertrag.beginn)) {
                    EventValidationResult(
                        commandId = event.commandId,
                        aggregateId = event.aggregateId,
                        valid = false,
                        validationType = ValidationType.Ungueltig,
                        exception = "Neues Ende liegt vor Vertrags-Beginn"
                    )
                } else {
                    EventValidationResult(
                        commandId = event.commandId,
                        aggregateId = event.aggregateId,
                        valid = true,
                        validationType = ValidationType.Gueltig,
                        exception = ""
                    )
                }
            }

            else -> EventValidationResult(
                commandId = "",
                aggregateId = "",
                valid = false,
                validationType = ValidationType.UngueltigeEingabe,
                exception = "Unbekannter Fehler"
            )
        }
}