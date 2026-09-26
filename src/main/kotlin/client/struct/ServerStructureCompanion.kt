package client.struct

import client.clients.AbstractGDClient
import client.GDClientApi

/**
 * Represents a structure that can be returned by Robtop's server.
 * This always must be the children of a `companion object`
 */
@GDClientApi
interface ServerStructureCompanion<out T : ServerStructure> {
    val separator: Char

    fun parse(rawString: String, client: AbstractGDClient): T
}