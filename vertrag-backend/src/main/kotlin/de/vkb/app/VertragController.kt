package de.vkb.app

import de.vkb.models.Vertrag
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post

@Controller("/vertrag")
class VertragController(private val vertragService: VertragService) {

    @Post("/erstellen", produces = [MediaType.APPLICATION_JSON], consumes = [MediaType.APPLICATION_JSON])
    fun vertragErstellen(@Body vertrag: Vertrag): HttpResponse<Vertrag> {
        vertragService.vertragErstellen(vertrag)
        return HttpResponse.created(vertrag)
    }

    @Post("/beginn", produces = [MediaType.APPLICATION_JSON], consumes = [MediaType.APPLICATION_JSON])
    fun beginnAendern(@Body vertrag: Vertrag): HttpResponse<Vertrag> {
        vertragService.beginnAendern(vertrag)
        return HttpResponse.created(vertrag)
    }
    @Post("/ende", produces = [MediaType.APPLICATION_JSON], consumes = [MediaType.APPLICATION_JSON])
    fun endeAendern(@Body vertrag: Vertrag): HttpResponse<Vertrag> {
        vertragService.endeAendern(vertrag)
        return HttpResponse.created(vertrag)
    }
}