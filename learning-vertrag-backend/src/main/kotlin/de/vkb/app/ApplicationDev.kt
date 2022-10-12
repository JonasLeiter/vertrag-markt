package de.vkb.app

import io.micronaut.runtime.Micronaut

fun main(args: Array<String>) {
    Micronaut.build()
        .args(*args)
        .packages("example.micronaut")
        .defaultEnvironments("dev")
        .start()
}