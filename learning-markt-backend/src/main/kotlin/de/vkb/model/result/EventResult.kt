package de.vkb.model.result

import de.vkb.model.aggregate.Markt
import de.vkb.model.event.Event
import de.vkb.model.validation.EventValidation

data class EventResult(
    val validation: EventValidation,
    val markt: Markt?,
    val event: Event?
) {
}