package de.vkb

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.Date

data class Vertrag(
    val bezeichnung: String,
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val beginn: Date,
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val ende: Date) {
}