package de.vkb.event.validations

import de.vkb.common.ValidationType
import de.vkb.event.events.VertragErstellt
import de.vkb.laser.es.dto.impl.EventAggregatorResult
import de.vkb.models.Vertrag
import jakarta.inject.Singleton

@Singleton
class VertragErstelltValidator {
    fun validate(event: VertragErstellt, vertragAggregate: Vertrag?): EventAggregatorResult<VertragErstellt, Vertrag, EventValidation> {
        var validation = EventValidation(
            commandId = event.commandId,
            aggregateId = event.aggregateId,
            isValid = false,
            validationType = ValidationType.UngueltigeEingabe,
            exception = "Unbekannter Fehler",
            hasErrors = true
        )

        return if (vertragAggregate == null) {
            validation = validation.copy(
                isValid = true,
                validationType = ValidationType.Gueltig,
                exception = "",
                hasErrors = false
            )
            val vertrag = Vertrag(
                id = event.aggregateId,
                bezeichnung = event.payload.bezeichnung,
                beginn = event.payload.beginn,
                ende = event.payload.ende
            )
            EventAggregatorResult(event, vertrag, validation)
        } else {
            validation = validation.copy(
                validationType = ValidationType.Ungueltig,
                exception = "Vertrag existiert bereits"
            )
            EventAggregatorResult(null, null, validation)
        }
    }
}