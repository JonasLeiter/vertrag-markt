package de.vkb.command

import de.vkb.command.commands.AendereBeginn
import de.vkb.command.commands.AendereEnde
import de.vkb.command.commands.Command
import de.vkb.command.commands.ErstelleVertrag
import de.vkb.common.ValidationType
import jakarta.inject.Singleton
import java.time.LocalDate

@Singleton
class CommandValidator() {

    fun validate(command: Command): CommandValidationResult =
        when (command) {
            is ErstelleVertrag -> {
                if (command.payload.ende.isBefore(command.payload.beginn)) {
                    CommandValidationResult(
                        validationId = command.commandId,
                        valid = false,
                        validationType = ValidationType.UngueltigeEingabe,
                        exception = "Ende liegt vor Beginn"
                    )
                } else {
                    CommandValidationResult(
                        validationId = command.commandId,
                        valid = true,
                        validationType = ValidationType.Gueltig,
                        exception = ""
                    )
                }
            }

            is AendereBeginn -> {
                if (command.payload.beginn.isBefore(LocalDate.now())) {
                    CommandValidationResult(
                        validationId = command.commandId,
                        valid = false,
                        validationType = ValidationType.UngueltigeEingabe,
                        exception = "Vertragsbeginn darf nicht in der Vergangenheit liegen"
                    )
                } else {
                    CommandValidationResult(
                        validationId = command.commandId,
                        valid = true,
                        validationType = ValidationType.Gueltig,
                        exception = ""
                    )
                }
            }

            is AendereEnde -> {
                if (command.payload.ende.isBefore(LocalDate.now())) {
                    CommandValidationResult(
                        validationId = command.commandId,
                        valid = false,
                        validationType = ValidationType.UngueltigeEingabe,
                        exception = "Vertragsende darf nicht in der Vergangenheit liegen"
                    )
                } else {
                    CommandValidationResult(
                        validationId = command.commandId,
                        valid = true,
                        validationType = ValidationType.Gueltig,
                        exception = ""
                    )
                }
            }

            else -> {
                CommandValidationResult(
                    validationId = command.commandId,
                    valid = false,
                    validationType = ValidationType.Ungueltig,
                    exception = "Unbekannter Fehler"
                )
            }
        }
}