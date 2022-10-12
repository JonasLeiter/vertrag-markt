package de.vkb.config

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("stores")
class StoreConfig {
    lateinit var stateStore: String
}