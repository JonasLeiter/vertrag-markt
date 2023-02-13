package de.vkb.event.validation

import de.vkb.common.ValidationType
import de.vkb.event.VertragErstelltResult
import de.vkb.event.events.VertragErstellt
import de.vkb.models.Vertrag
import jakarta.inject.Singleton

@Singleton
class CreatedValidator {
    fun validate(event: VertragErstellt, vertragAggregate: Vertrag?): VertragErstelltResult {
        var validation = EventValidation(
            commandId = event.commandId,
            aggregateId = event.aggregateId,
            valid = false,
            validationType = ValidationType.UngueltigeEingabe,
            exception = "Unbekannter Fehler"
        )

        return if (vertragAggregate == null) {
            validation = validation.copy(
                valid = true,
                validationType = ValidationType.Gueltig,
                exception = ""
            )
            val vertrag = Vertrag(
                id = event.aggregateId,
                bezeichnung = event.payload.bezeichnung,
                beginn = event.payload.beginn,
                ende = event.payload.ende
            )
            VertragErstelltResult(event, vertrag, validation)
        } else {
            validation = validation.copy(
                validationType = ValidationType.Ungueltig,
                exception = "Vertrag existiert bereits"
            )
            VertragErstelltResult(null, null, validation)
        }
    }
}