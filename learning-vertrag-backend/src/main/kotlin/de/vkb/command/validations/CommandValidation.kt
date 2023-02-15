package de.vkb.command.validations

import de.vkb.common.ValidationResult
import de.vkb.common.ValidationType
import de.vkb.laser.es.model.Feedback

data class CommandValidation (
    override val commandId: String,
    override val aggregateId: String,
    override val isValid: Boolean,
    override val validationType: ValidationType,
    override val exception: String,
    override val hasErrors: Boolean
) : Feedback, ValidationResult

