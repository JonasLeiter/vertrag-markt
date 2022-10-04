package de.vkb

import de.vkb.models.Vertrag
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post

@Controller("/vertrag")
class VertragController(private val vertragService: VertragService) {

    @Post(produces = [MediaType.APPLICATION_JSON], consumes = [MediaType.APPLICATION_JSON])
    fun send(@Body vertrag: Vertrag): HttpResponse<Vertrag> {
        vertragService.send(vertrag)
        return HttpResponse.created(vertrag)
    }
}