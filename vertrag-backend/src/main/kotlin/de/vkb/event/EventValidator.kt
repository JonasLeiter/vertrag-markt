package de.vkb.event

import de.vkb.event.events.BeginnGeandert
import de.vkb.event.events.EndeGeandert
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
                        validationId = event.eventId,
                        valid = true,
                        validationType = ValidationType.Gueltig,
                        exception = ""
                    )
                } else {
                    EventValidationResult(
                        validationId = event.eventId,
                        valid = false,
                        validationType = ValidationType.Ungueltig,
                        exception = "Vertrag existiert bereits"
                    )
                }
            }
            is BeginnGeandert -> {
                if (vertrag == null) {
                    EventValidationResult(
                        validationId = event.eventId,
                        valid = false,
                        validationType = ValidationType.Ungueltig,
                        exception = "Vertrag existiert nicht"
                    )
                } else if (event.payload.beginn.isAfter(vertrag.ende)) {
                    EventValidationResult(
                        validationId = event.eventId,
                        valid = false,
                        validationType = ValidationType.Ungueltig,
                        exception = "Neuer Beginn liegt vor Vertrags-Ende"
                    )
                } else {
                    EventValidationResult(
                        validationId = event.eventId,
                        valid = true,
                        validationType = ValidationType.Gueltig,
                        exception = ""
                    )
                }
            }
            is EndeGeandert -> {
                if (vertrag == null) {
                    EventValidationResult(
                        validationId = event.eventId,
                        valid = false,
                        validationType = ValidationType.Ungueltig,
                        exception = "Vertrag existiert nicht"
                    )
                } else if (event.payload.ende.isBefore(vertrag.beginn)) {
                    EventValidationResult(
                        validationId = event.eventId,
                        valid = false,
                        validationType = ValidationType.Ungueltig,
                        exception = "Neues Ende liegt vor Vertrags-Beginn"
                    )
                } else {
                    EventValidationResult(
                        validationId = event.eventId,
                        valid = true,
                        validationType = ValidationType.Gueltig,
                        exception = ""
                    )
                }
            }
            else -> EventValidationResult(
                validationId = "",
                valid = false,
                validationType = ValidationType.UngueltigeEingabe,
                exception = "Unbekannter Fehler"
            )
        }
}