package client

import client.clients.ResponseHandler
import exceptions.ServerErrorException

@GDClientApi
object ResponseHandlers {
    private const val BAN_TIME_RESPONSE_CAP = 3020399

    /**
     * The response handler for the generic `-1` error code
     */
    val GENERIC: ResponseHandler = {
        if (it == "-1")
            throw ServerErrorException.genericError()
    }

    val COMMENT: ResponseHandler = {
        when (it) {
            "-1" -> throw ServerErrorException.genericError()
            "-10" -> throw ServerErrorException(-10, "Client is permanently comment banned")
        }

        if (it.startsWith("temp_")) {
            val splitted = it.split("_")
            val time = splitted[1]
            val reason = splitted[2]

            val timeStr = if (time.toInt() >= BAN_TIME_RESPONSE_CAP)
                ">=$time"
            else
                time

            throw ServerErrorException("Client is temporarily banned from posting comments for $timeStr seconds: $reason")
        }
    }
}