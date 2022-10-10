package de.vkb.event

import de.vkb.models.Vertrag
import jakarta.inject.Singleton

@Singleton
class EventValidator {

    fun validate(event: Event?, vertrag: Vertrag?): EventValidationResult =

        when (event) {
            is VertragErstellt -> {
                if (vertrag == null) {
                    EventValidationResult(
                        eventId = event.eventId,
                        valid = true,
                        validationType = ValidationType.Gueltig,
                        exception = ""
                    )
                } else {
                    EventValidationResult(
                        eventId = event.eventId,
                        valid = false,
                        validationType = ValidationType.Ungueltig,
                        exception = "Vertrag already exists"
                    )
                }
            }

            is BeginnGeandert -> {
                if (vertrag == null) {
                    EventValidationResult(
                        eventId = event.eventId,
                        valid = false,
                        validationType = ValidationType.Ungueltig,
                        exception = "Vertrag does not exist"
                    )
                } else {
                    EventValidationResult(
                        eventId = event.eventId,
                        valid = true,
                        validationType = ValidationType.Gueltig,
                        exception = ""
                    )
                }
            }

            is EndeGeandert -> {
                if (vertrag == null) {
                    EventValidationResult(
                        eventId = event.eventId,
                        valid = false,
                        validationType = ValidationType.Ungueltig,
                        exception = "Vertrag does not exist"
                    )
                } else {
                    EventValidationResult(
                        eventId = event.eventId,
                        valid = true,
                        validationType = ValidationType.Gueltig,
                        exception = ""
                    )
                }
            }

            else -> EventValidationResult(
                eventId = "",
                valid = false,
                validationType = ValidationType.UngueltigeEingabe,
                exception = "Unknown Error"
            )
        }


}