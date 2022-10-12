package de.vkb.model.aggregate

import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.LocalDate

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "type")
data class Markt(
    val id: String,
    val ort: String,
    val datum: LocalDate
)