package de.vkb.event.validation

import de.vkb.common.ValidationResult
import de.vkb.common.ValidationType
import de.vkb.laser.es.model.Feedback

data class EventValidation(
    override val commandId: String,
    override val aggregateId: String,
    override val valid: Boolean,
    override val validationType: ValidationType,
    override val exception: String,
    val errors: List<Error> = emptyList()
) : Feedback, ValidationResult {
    override val hasErrors: Boolean
        get() = errors.isNotEmpty()
}