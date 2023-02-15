package de.vkb.event.validations

import de.vkb.common.ValidationType
import de.vkb.event.events.EndeGeaendert
import de.vkb.laser.es.dto.impl.EventAggregatorResult
import de.vkb.models.Vertrag
import jakarta.inject.Singleton

@Singleton
class EndeGeaendertValidator {
    fun validate(event: EndeGeaendert, vertragAggregate: Vertrag?): EventAggregatorResult<EndeGeaendert, Vertrag, EventValidation> {
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
                validationType = ValidationType.Ungueltig,
                exception = "Vertrag existiert nicht"
            )
            EventAggregatorResult(null, null, validation)
        } else if (event.payload.ende.isBefore(vertragAggregate.beginn)) {
            validation = validation.copy(
                validationType = ValidationType.Ungueltig,
                exception = "Neues Ende liegt vor Vertrags-Beginn"
            )
            EventAggregatorResult(null, null, validation)
        } else {
            validation = validation.copy(
                isValid = true,
                validationType = ValidationType.Gueltig,
                exception = "",
                hasErrors = false
            )
            val vertrag = vertragAggregate.copy(
                ende = event.payload.ende
            )
            EventAggregatorResult(event, vertrag, validation)
        }
    }
}