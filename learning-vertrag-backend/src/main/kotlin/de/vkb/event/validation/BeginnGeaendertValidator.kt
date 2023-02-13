package de.vkb.event.validation

import de.vkb.common.ValidationType
import de.vkb.event.BeginnGeaendertResult
import de.vkb.event.events.BeginnGeaendert
import de.vkb.models.Vertrag
import jakarta.inject.Singleton

@Singleton
class BeginnGeaendertValidator {
    fun validate(event: BeginnGeaendert, vertragAggregate: Vertrag?): BeginnGeaendertResult {
        var validation = EventValidation(
            commandId = event.commandId,
            aggregateId = event.aggregateId,
            valid = false,
            validationType = ValidationType.UngueltigeEingabe,
            exception = "Unbekannter Fehler"
        )

        return if (vertragAggregate == null) {
            validation = validation.copy(
                validationType = ValidationType.Ungueltig,
                exception = "Vertrag existiert nicht"
            )
            BeginnGeaendertResult(null, null, validation)
        } else if (event.payload.beginn.isAfter(vertragAggregate.ende)) {
            validation = validation.copy(
                validationType = ValidationType.Ungueltig,
                exception = "Neuer Beginn liegt nach Vertrags-Ende"
            )
            BeginnGeaendertResult(null, null, validation)
        } else {
            validation = validation.copy(
                validationType = ValidationType.Gueltig,
                valid = true,
                exception = ""
            )
            val vertrag = vertragAggregate.copy(
                beginn = event.payload.beginn
            )
            BeginnGeaendertResult(event, vertrag, validation)
        }
    }
}