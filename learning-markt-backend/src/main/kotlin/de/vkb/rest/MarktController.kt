package de.vkb.rest

import de.vkb.model.command.AendereDatumPayload
import de.vkb.model.command.AendereOrtPayload
import de.vkb.model.command.ErstelleMarktPayload
import de.vkb.model.result.MarktResult
import de.vkb.service.MarktService
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.validation.Validated
import javax.validation.Valid

@Controller("/markt")
@Validated
class MarktController(private val marktService: MarktService
) {

    @Post(consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun createMarkt(@Body @Valid markt: ErstelleMarktPayload): HttpResponse<MarktResult>{
        val savedMarkt = marktService.saveMarkt(markt)
        return HttpResponse.created(savedMarkt)
    }

    @Put("/ort")
    fun changeOrt(@Body @Valid markt: AendereOrtPayload): HttpResponse<MarktResult>{
        val savedMarkt = marktService.changeOrt(markt)
        return HttpResponse.created(savedMarkt)
    }

    @Put("/datum")
    fun changeDatum(@Body @Valid markt: AendereDatumPayload): HttpResponse<MarktResult>{
        val savedMarkt = marktService.changeDatum(markt)
        return HttpResponse.created(savedMarkt)
    }
}