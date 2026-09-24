package client.struct

import client.AbstractGDClient
import client.GDClientApi
import editor.objects.GenericGdObject

/**
 * Represents a structure that can be returned by Robtop's server.
 */
@GDClientApi
interface ServerStructure : GenericGdObject {
    val client: AbstractGDClient

    /**
     * Get the geometry dash raw string representing this object.
     * Generally on server structures **there's no need to use this**
     * @return the raw string representing this object
     */
    override fun asRawString(): String =
        super.asRawString()
}

@OptIn(GDClientApi::class)
inline fun ServerStructure.useClient(func: AbstractGDClient.(client: AbstractGDClient) -> Unit) = func(this.client, this.client)
