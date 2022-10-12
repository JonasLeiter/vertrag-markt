package de.vkb.model.event

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "type")
interface Event{
    val commandId: String
    val payload: Any
    val aggregateIdentifier: String
}



