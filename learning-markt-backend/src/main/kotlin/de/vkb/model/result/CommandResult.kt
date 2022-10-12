package de.vkb.model.result

import de.vkb.model.event.Event
import de.vkb.model.validation.CommandValidation

data class CommandResult(
    val validation: CommandValidation,
    val event: Event?
) {

}