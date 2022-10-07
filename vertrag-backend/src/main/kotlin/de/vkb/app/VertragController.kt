package de.vkb.app

import de.vkb.command.AendereBeginnPayload
import de.vkb.command.AendereEndePayload
import de.vkb.command.ErstelleVertragPayload
import de.vkb.models.Vertrag
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post

@Controller("/vertrag")
class VertragController(private val vertragService: VertragService) {

    @Post("/erstellen", produces = [MediaType.APPLICATION_JSON], consumes = [MediaType.APPLICATION_JSON])
    fun vertragErstellen(@Body payload: ErstelleVertragPayload): HttpResponse<Vertrag> {
        val vertrag = vertragService.vertragErstellen(payload)
        return HttpResponse.created(vertrag)
    }
    @Post("/beginn", produces = [MediaType.APPLICATION_JSON], consumes = [MediaType.APPLICATION_JSON])
    fun beginnAendern(@Body payload: AendereBeginnPayload): HttpResponse<AendereBeginnPayload> {
        vertragService.beginnAendern(payload)
        return HttpResponse.created(payload)
    }
    @Post("/ende", produces = [MediaType.APPLICATION_JSON], consumes = [MediaType.APPLICATION_JSON])
    fun endeAendern(@Body payload: AendereEndePayload): HttpResponse<AendereEndePayload> {
        vertragService.endeAendern(payload)
        return HttpResponse.created(payload)
    }
}