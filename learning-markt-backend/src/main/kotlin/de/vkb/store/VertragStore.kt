package de.vkb.store

import de.vkb.config.StoreConfig
import de.vkb.model.aggregate.Markt
import de.vkb.model.aggregate.Vertrag
import io.micronaut.configuration.kafka.streams.InteractiveQueryService
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.apache.kafka.streams.state.QueryableStoreTypes

@Singleton
class VertragStore(
    private val storeConfig: StoreConfig
) {

    @Inject
    lateinit var interactiveQueryService: InteractiveQueryService

    fun getVertrag(id: String): Vertrag? =
        interactiveQueryService
            .getQueryableStore(
                storeConfig.vertragStore,
                QueryableStoreTypes.keyValueStore<String, Vertrag>())
            .map { it[id] }.orElse(null)
}