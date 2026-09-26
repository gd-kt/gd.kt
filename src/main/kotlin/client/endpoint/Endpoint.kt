package client.endpoint

import client.GDClientApi
import client.clients.ResponseHandler
import okhttp3.HttpUrl

@GDClientApi
data class Endpoint(val endpoint: String, val responseHandler: ResponseHandler = {}) {
    fun resolve(url: HttpUrl): HttpUrl =
        url.resolve("database/" + this.endpoint + ".php")!!
}