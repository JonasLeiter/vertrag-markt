package de.vkb.model.validation

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "type")
interface Validation{
    val commandId: String
    val aggregateIdentifier: String
    val message: String
    val hasErrors: Boolean
}