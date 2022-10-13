package de.vkb.event.events

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
interface Event {
    val commandId: String
    val aggregateId: String
    val payload: Any
}