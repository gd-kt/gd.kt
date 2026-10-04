package client.clients

import client.Credentials
import client.GDClientApi
import client.Platform
import client.endpoint.Endpoints
import client.struct.LevelCommentStructure
import client.struct.UserInfo
import editor.rawstring.serializing.Parsable
import editor.rawstring.serializing.Parsable.Companion.listParsable
import editor.rawstring.serializing.Serializers
import editor.rawstring.serializing.hook
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
    @JvmOverloads
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

    @JvmOverloads
    fun getComments(levelID: Int, page: Int = 0, isList: Boolean = false, asyncCallback: CallbackWithData<List<LevelCommentStructure>>) {
        this.throwIfLoggedOut()
        this.executeRequest(
            Parsable.fromServerStruct(LevelCommentStructure, this).listParsable('|').hook { rawString ->
                // Everything that's after the '#' is the
                // "page info". We get rid of it
                // to not clutter the response for the moment
                //
                // (ex: having a data class wrapping
                // List<LevelCommentStructure> and the page info)
                // (TODO: Add this wrapper)
                //
                // see: https://boomlings.dev/endpoints/lists/getGJLevelLists#response
                rawString.removeRange(rawString.indexOf('#'), rawString.length - 1)
            },
            Endpoints.GET_COMMENTS,
            mapOf(
                Pair("levelID", if (isList) -levelID else levelID),
                Pair("page", page),
            ),
            asyncCallback = asyncCallback
        )
    }
}
