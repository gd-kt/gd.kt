package client.clients

import client.Credentials
import client.GDClientApi
import client.Platform
import client.endpoint.Endpoints
import client.struct.UserInfo
import editor.rawstring.serializing.Serializers
import okhttp3.HttpUrl
import utils.toCompactedString
import kotlin.io.encoding.Base64

/**
 * Represents an **asynchronous** geometry dash client.
 * @see GDClient
 */
@GDClientApi
class AsyncGDClient(
    credentials: Credentials? = null,
    url: HttpUrl = DEFAULT_URL,

    gameVersion: UInt = GAME_VERSION,
    binaryVersion: UInt = BINARY_VERSION,
    platform: Platform = Platform.get()
) : AbstractGDClient(credentials, url, gameVersion, binaryVersion, platform) {
    fun getUserInfo(accountID: Int, asyncCallback: CallbackWithData<UserInfo>) {
        this.executeRequest(
            UserInfo,
            Endpoints.LOGIN,
            mapOf(
                Pair("targetAccountID", accountID)
            ),
            asyncCallback = asyncCallback
        )
    }

    /**
     * The callback returns the ID of the sent comment
     */
    fun postAccountComment(message: String, asyncCallback: CallbackWithData<Int>) {
        this.throwIfLoggedOut()
        this.executeRequest(
            Serializers.INT,
            Endpoints.UPLOAD_ACCOUNT_COMMENT,
            mapOf(
                Pair("comment", Base64.UrlSafe.encode(message.toByteArray())),
                Pair("accountID", this.accountID!!)
            ),
            asyncCallback = asyncCallback
        )
    }

    /**
     * The callback returns the ID of the sent comment
     */
    fun postComment(message: String, levelID: Int, percentage: Float = 0f, isList: Boolean = false, asyncCallback: CallbackWithData<Int>) {
        this.throwIfLoggedOut()
        val encodedMessage = Base64.UrlSafe.encode(message.toByteArray())
        val actualLevelID = if (isList) -levelID else levelID
        this.executeRequest(
            Serializers.INT,
            Endpoints.UPLOAD_COMMENT,
            mapOf(
                Pair("comment", encodedMessage),
                Pair("accountID", this.accountID!!),
                Pair("userName", this.credentials!!.username),
                Pair("levelID", actualLevelID),
                Pair("percent", percentage),
                Pair("chk", createCHK(
                    this.credentials.username,
                    encodedMessage,
                    actualLevelID,
                    percentage.toCompactedString(),
                    0, // = commentType = level
                    key = XorKey.COMMENT_INTEGRITY
                ))
            ),
            asyncCallback = asyncCallback
        )
    }

    fun deleteComment(commentID: Int, levelID: Int, asyncCallback: CallbackWithData<Boolean>) {
        this.throwIfLoggedOut()
        this.executeRequest(
            Serializers.BOOLEAN,
            Endpoints.DELETE_COMMENT,
            mapOf(
                Pair("accountID", this.accountID!!),
                Pair("commentID", commentID),
                Pair("levelID", levelID)
            ),
            asyncCallback = asyncCallback
        )
    }

    fun deleteAccountComment(commentID: Int, asyncCallback: CallbackWithData<Boolean>) {
        this.throwIfLoggedOut()
        this.executeRequest(
            Serializers.BOOLEAN,
            Endpoints.DELETE_ACCOUNT_COMMENT,
            mapOf(
                Pair("accountID", this.accountID!!),
                Pair("commentID", commentID),
            ),
            asyncCallback = asyncCallback
        )
    }
}
