package de.vkb.event

import de.vkb.common.ValidationResult
import de.vkb.common.ValidationType

data class EventValidationResult(
    override val validationId: String,
    override val valid: Boolean,
    override val validationType: ValidationType,
    override val exception: String
) : ValidationResult