package de.vkb.command

import de.vkb.common.ValidationResult
import de.vkb.common.ValidationType

data class CommandValidationResult (
    override val commandId: String,
    override val aggregateId: String,
    override val valid: Boolean,
    override val validationType: ValidationType,
    override val exception: String
) : ValidationResult

