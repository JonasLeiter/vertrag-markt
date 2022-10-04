package de.vkb.command

import de.vkb.Vertrag
import java.util.UUID

class VertragCommand(override val payload: Vertrag) : Command {

    override val commandId = UUID.randomUUID().toString()

    override fun execute(): ExecutionResult {
        TODO("Not yet implemented")
    }


}