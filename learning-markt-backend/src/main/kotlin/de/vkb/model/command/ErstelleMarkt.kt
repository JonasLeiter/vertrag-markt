package de.vkb.model.command

import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.core.annotation.Introspected
import java.time.LocalDate
import javax.validation.constraints.NotBlank

data class ErstelleMarkt(
    override val commandId: String,
    override val payload: ErstelleMarktPayload,
    override val aggregateIdentifier: String,
):Command

@Introspected
data class ErstelleMarktPayload(
    @field:NotBlank
    val ort: String,
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    @field:NotBlank
    val datum: LocalDate)