package de.vkb.model.command

import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.core.annotation.Introspected
import java.time.LocalDate
import javax.validation.constraints.NotBlank

data class AendereDatum(
    override val commandId: String,
    override val payload: AendereDatumPayload,
    override val aggregateIdentifier: String,
):Command

@Introspected
data class AendereDatumPayload(
    @field:NotBlank
    val id: String,
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    @field:NotBlank
    val datum: LocalDate)