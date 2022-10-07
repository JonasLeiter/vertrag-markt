package de.vkb.command

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
interface Command {
    val commandId: String
    val aggregateId: String
    val payload: Any
}