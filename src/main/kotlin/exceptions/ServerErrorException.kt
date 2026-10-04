package exceptions

/**
 * An exception used whenever Geometry Dash's servers return an error
 * @see client.clients.GDClient
 * @see client.clients.AsyncGDClient
 */
class ServerErrorException(message: String, val errorCode: Int? = null) : GdDotKtException(message) {
    companion object {
        fun genericError(): ServerErrorException =
            ServerErrorException("Server returned error code -1: Generic Error", -1)
    }

    constructor(errorCode: Int, errorMessage: String) : this("Server returned error code $errorCode: $errorMessage", errorCode)
}