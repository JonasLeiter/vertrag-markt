package de.vkb.common

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
interface ValidationResult {
    val validationId: String
    val valid: Boolean
    val validationType: ValidationType
    val exception: String
}
