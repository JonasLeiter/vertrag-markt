package de.vkb.model.validation

import de.vkb.laser.es.model.Feedback

data class EventValidation(
    override val commandId: String,
    override val isValid: Boolean,
    override val aggregateIdentifier: String,
    override val message: String,
    val systemErrors: List<String>? = null
): Feedback, Validation {
    override val hasErrors: Boolean
        get() = !systemErrors.isNullOrEmpty()
}
