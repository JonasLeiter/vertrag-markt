package de.vkb.event

//@KafkaListener
class EventAggregator() {

  //  @Topic("learning-vertrag-internal-event")
    fun receive(internalEvent: InternalEvent) {
//        // validate event
//
//
//
//        // update aggregate
//
//
//
//        // transform to external event
//        val externalEvent = VertragExternalEvent(
//            id = internalEvent.id,
//            aggregateIdentifier = internalEvent.aggregateIdentifier.copy(),
//            payload = internalEvent.payload.copy(),
//            type = EventType.VERTRAG_ERSTELLT
//        )
//        // external event actually changes the domain
//        externalEvent.payload.id = internalEvent.aggregateIdentifier.id
//
//
//        // produce to external-event-topic
//        externalEventProducer.send(externalEvent.aggregateIdentifier.id, externalEvent)
    }

}