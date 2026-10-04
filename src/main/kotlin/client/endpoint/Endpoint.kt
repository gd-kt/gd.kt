package client.endpoint

import client.GDClientApi
import client.ResponseHandlers
import client.clients.ResponseHandler
import okhttp3.HttpUrl

/**
 * Defines an endpoint used on Geometry Dash's servers
 * @see Endpoints
 */
@GDClientApi
data class Endpoint(val endpoint: String, val responseHandler: ResponseHandler = ResponseHandlers.GENERIC) {
    fun resolve(url: HttpUrl): HttpUrl =
        url.resolve("database/" + this.endpoint + ".php")!!
}