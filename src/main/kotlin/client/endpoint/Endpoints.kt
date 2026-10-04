package client.endpoint

import client.GDClientApi
import client.ResponseHandlers

/**
 * Contains a list of endpoints used by `gd.kt`'s client api
 * @see Endpoint
 * @see client.clients.GDClient
 * @see client.clients.AsyncGDClient
 */
@GDClientApi
object Endpoints {
    // User
    val GET_USER_INFO = Endpoint("getGJUserInfo20")

    // Account
    val LOGIN = Endpoint("accounts/loginGJAccount")

    // Comments
    val UPLOAD_COMMENT = Endpoint("uploadGJComment21", ResponseHandlers.COMMENT)
    val DELETE_COMMENT = Endpoint("deleteGJComment20")
    val UPLOAD_ACCOUNT_COMMENT = Endpoint("uploadGJAccComment20", ResponseHandlers.COMMENT)
    val DELETE_ACCOUNT_COMMENT = Endpoint("deleteGJAccComment20")
    val GET_COMMENTS = Endpoint("getGJComments21")
}