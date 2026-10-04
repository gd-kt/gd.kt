package client.clients

import XorKey
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
 * Represents a **synchronous** geometry dash client.
 * @see AsyncGDClient
 */
@GDClientApi
class GDClient(
    credentials: Credentials? = null,
    url: HttpUrl = DEFAULT_URL,

    gameVersion: UInt = GAME_VERSION,
    binaryVersion: UInt = BINARY_VERSION,
    platform: Platform = Platform.get()
) : AbstractGDClient(credentials, url, gameVersion, binaryVersion, platform) {
    fun getUserInfo(accountID: Int): Result<UserInfo> =
        this.executeRequest(
            UserInfo,
            Endpoints.GET_USER_INFO,
            mapOf(
                Pair("targetAccountID", accountID)
            ),
            asyncCallback = null
        )

    /**
     * @return the ID of the sent comment
     */
    fun postAccountComment(message: String): Result<Int> {
        this.throwIfLoggedOut()
        return this.executeRequest(
            Serializers.INT,
            Endpoints.UPLOAD_ACCOUNT_COMMENT,
            mapOf(
                Pair("comment", Base64.UrlSafe.encode(message.toByteArray())),
                Pair("accountID", this.accountID!!)
            ),
            asyncCallback = null
        )
    }

    /**
     * @return the ID of the sent comment
     */
    fun postComment(message: String, levelID: Int, percentage: Float = 0f, isList: Boolean = false): Result<Int> {
        this.throwIfLoggedOut()
        val encodedMessage = Base64.UrlSafe.encode(message.toByteArray())
        return this.executeRequest(
            Serializers.INT,
            Endpoints.UPLOAD_COMMENT,
            mapOf(
                Pair("comment", encodedMessage),
                Pair("accountID", this.accountID!!),
                Pair("userName", this.credentials!!.username),
                Pair("levelID", levelID),
                Pair("percent", percentage),
                Pair("chk", createCHK(
                    this.credentials.username,
                    encodedMessage,
                    if (isList) -levelID else levelID,
                    percentage.toCompactedString(),
                    0, // = commentType = level
                    key = XorKey.COMMENT_INTEGRITY
                ))
            ),
            asyncCallback = null
        )
    }

    fun deleteComment(commentID: Int, levelID: Int): Result<Boolean> {
        this.throwIfLoggedOut()
        return this.executeRequest(
            Serializers.BOOLEAN,
            Endpoints.DELETE_COMMENT,
            mapOf(
                Pair("accountID", this.accountID!!),
                Pair("commentID", commentID),
                Pair("levelID", levelID)
            )
        )
    }

    fun deleteAccountComment(commentID: Int): Result<Boolean> {
        this.throwIfLoggedOut()
        return this.executeRequest(
            Serializers.BOOLEAN,
            Endpoints.DELETE_ACCOUNT_COMMENT,
            mapOf(
                Pair("accountID", this.accountID!!),
                Pair("targetAccountID", this.accountID!!),
                Pair("commentID", commentID)
            )
        )
    }
}

