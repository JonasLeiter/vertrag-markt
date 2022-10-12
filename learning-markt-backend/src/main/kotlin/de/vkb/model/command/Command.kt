package de.vkb.model.command

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "type")
interface Command {
    val commandId: String
    val payload: Any
    val aggregateIdentifier: String
}