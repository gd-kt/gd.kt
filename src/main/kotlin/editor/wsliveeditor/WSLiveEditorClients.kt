package editor.wsliveeditor

import editor.rawstring.RawStringFactory
import editor.rawstring.RawStringable
import exceptions.WSLiveEditorException
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import okhttp3.*
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.seconds

sealed class WSLiveEditorClient(val port: UShort = DEFAULT_PORT, protected val client: OkHttpClient = DEFAULT_CLIENT) : AutoCloseable {
    companion object {
        @JvmStatic
        @get:JvmName("getDefaultClient")
        val DEFAULT_CLIENT
            get() = OkHttpClient.Builder()
                .webSocketCloseTimeout(5.seconds)
                .connectTimeout(1.seconds) // localhost requests should be very fast
                .readTimeout(1.seconds)
                .callTimeout(1.seconds)
                .build()

        const val DEFAULT_PORT: UShort = 1313u

        @JvmStatic
        fun createAction(name: String, stringData: Map<String, String> = mapOf(), intData: Map<String, Int> = mapOf()): String {
            val jsonObj = buildJsonObject {
                put("action", JsonPrimitive(name))
                stringData.forEach { (key, value) ->
                    put(key, JsonPrimitive(value))
                }

                intData.forEach { (key, value) ->
                    put(key, JsonPrimitive(value))
                }
            }


            return Json.encodeToString(jsonObj)
        }
    }

    protected fun createRequest(): Request = Request.Builder()
        .url("ws://127.0.0.1:${this.port}")
        .build()

    protected fun createListener(action: String, close: Boolean = false, responseHandler: (response: WSLiveEditorResponse) -> Unit): WebSocketListener =
        object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send(action)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                if (close)
                    webSocket.close(1000, null)
                    // println("Closed websocket: ${webSocket.close(1000, null)}")

                val response = Json.decodeFromString<WSLiveEditorResponse>(text)
                response.throwIfError()

                responseHandler(response)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                throw WSLiveEditorException("Caught an error while trying to do a WSLiveEditor operation", t)
            }
        }

    @Serializable
    protected data class WSLiveEditorResponse(
        val status: String,
        @SerialName("error") val errorMessage: String? = null,
        @SerialName("response") val successResponse: String? = null
    ) {
        fun isError(): Boolean =
            this.errorMessage == null || this.errorMessage.isNotEmpty()

        fun throwIfError() {
            if (this.isError())
                throw WSLiveEditorException("WSLiveEditor error: ${this.errorMessage}")
        }
    }
}

/**
 * Represents an **asynchronous** [WSLiveEditor](https://geode-sdk.org/mods/iandyhd3.wsliveeditor) client.
 *
 * [WSLiveEditor](https://geode-sdk.org/mods/iandyhd3.wsliveeditor) is a **[geode](https://geode-sdk.org)** mod
 * that allows to request info/modify info on, the editor.
 * @see WSLiveEditor
 */
class AsyncWSLiveEditor(port: UShort = DEFAULT_PORT, client: OkHttpClient = DEFAULT_CLIENT) : WSLiveEditorClient(port, client) {
    /**
     * Adds objects into the editor.
     * If multiple objects are going to get added, they must be separated by semicolons
     * @throws WSLiveEditorException if any error happens during the execution
     */
    fun addObjects(objects: String, asyncCallback: AsyncCallback<Boolean> = {}) {
        this.client.newWebSocket(
            this.createRequest(),
            this.createListener(createAction("ADD_OBJECTS", stringData = mapOf(Pair("objects", objects))), true) {
                asyncCallback(!it.isError())
            }
        )
    }

    /**
     * Adds objects into the editor.
     * If multiple objects are going to get added, they must be separated by semicolons
     * @throws WSLiveEditorException if any error happens during the execution
     */
    @JvmName("addStringObjects")
    fun addObjects(objects: Collection<String>, asyncCallback: AsyncCallback<Boolean> = {}) =
        this.addObjects(RawStringFactory.joinRawStrings(objects), asyncCallback)

    /**
     * Adds objects into the editor.
     * If multiple objects are going to get added, they must be separated by semicolons
     * @throws WSLiveEditorException if any error happens during the execution
     */
    fun addObject(rawStringableObj: RawStringable, asyncCallback: AsyncCallback<Boolean> = {}) =
        this.addObjects(rawStringableObj.asRawString(), asyncCallback)

    /**
     * Adds objects into the editor.
     * If multiple objects are going to get added, they must be separated by semicolons
     * @throws WSLiveEditorException if any error happens during the execution
     */
    fun addObjects(objects: Collection<RawStringable>, asyncCallback: AsyncCallback<Boolean> = {}) =
        this.addObjects(RawStringFactory.joinRawStrings(objects), asyncCallback)

    /**
     * Removes all objects in the editor with the given group
     * @throws WSLiveEditorException if any error happens during the execution
     */
    fun removeObjects(group: UInt, asyncCallback: AsyncCallback<Boolean> = {}) {
        this.client.newWebSocket(
            this.createRequest(),
            // "{\"action\": \"REMOVE_OBJECTS\", \"group\": $group}"
            this.createListener(createAction("REMOVE_OBJECTS", intData = mapOf(Pair("group", group.toInt()))), true) {
                asyncCallback(!it.isError())
            }
        )
    }

    /**
     * Removes all objects in the editor with the given group
     * @throws WSLiveEditorException if any error happens during the execution
     */
    @JvmName("removeObjectsJava")
    fun removeObjects(group: Int, asyncCallback: AsyncCallback<Boolean> = {}) =
        this.removeObjects(group.toUInt(), asyncCallback)

    /**
     * Gets the currently opened level's raw string
     * @throws WSLiveEditorException if any error happens during the execution
     */
    fun getLevelString(asyncCallback: AsyncCallback<String>) {
        this.client.newWebSocket(
            this.createRequest(),
            this.createListener(createAction("GET_LEVEL_STRING"), true) {
                asyncCallback(it.successResponse!!)
            }
        )
    }

    /**
     * Sets the level string of the currently opened level
     * @throws WSLiveEditorException if any error happens during the execution
     */
    fun setLevelString(levelString: String, asyncCallback: AsyncCallback<Boolean> = {}) {
        this.client.newWebSocket(
            this.createRequest(),
            this.createListener(createAction("REPLACE_LEVEL_STRING", stringData = mapOf(Pair("levelString", levelString))), true) {
                asyncCallback(!it.isError())
            }
        )
    }

    override fun close() {
        // We need to do this so all the websocket threads
        // actually finish executing
        this.client.dispatcher.executorService.shutdown()
        this.client.connectionPool.evictAll()
    }

    fun interface AsyncCallback<in T> {
        operator fun invoke(responseValue: T)
    }
}

/**
 * Represents a **synchronous** [WSLiveEditor](https://geode-sdk.org/mods/iandyhd3.wsliveeditor) client.
 *
 * [WSLiveEditor](https://geode-sdk.org/mods/iandyhd3.wsliveeditor) is a **[geode](https://geode-sdk.org)** mod
 * that allows to request info/modify info on, the editor.
 * @see WSLiveEditor
 */
class WSLiveEditor(port: UShort = DEFAULT_PORT, client: OkHttpClient = DEFAULT_CLIENT) : WSLiveEditorClient(port, client) {
    val asyncClient = AsyncWSLiveEditor(this.port, this.client)

    private fun <T> syncCall(funcCall: (continuation: CancellableContinuation<T>, client: AsyncWSLiveEditor) -> Unit): T =
        runBlocking {
            suspendCancellableCoroutine {
                funcCall(it, this@WSLiveEditor.asyncClient)
            }
        }

    /**
     * Adds objects into the editor.
     * If multiple objects are going to get added, they must be separated by semicolons
     * @throws WSLiveEditorException if any error happens during the execution
     */
    fun addObjects(objects: String): Boolean =
        syncCall { continuation, client ->
            client.addObjects(objects) {
                continuation.resume(it)
            }
        }

    /**
     * Adds objects into the editor.
     * If multiple objects are going to get added, they must be separated by semicolons
     * @throws WSLiveEditorException if any error happens during the execution
     */
    @JvmName("addStringObjects")
    fun addObjects(objects: Collection<String>): Boolean =
        this.addObjects(RawStringFactory.joinRawStrings(objects))

    /**
     * Adds objects into the editor.
     * If multiple objects are going to get added, they must be separated by semicolons
     * @throws WSLiveEditorException if any error happens during the execution
     */
    fun addObject(rawStringableObj: RawStringable): Boolean =
        this.addObjects(rawStringableObj.asRawString())

    /**
     * Adds objects into the editor.
     * If multiple objects are going to get added, they must be separated by semicolons
     * @throws WSLiveEditorException if any error happens during the execution
     */
    fun addObjects(objects: Collection<RawStringable>): Boolean =
        this.addObjects(RawStringFactory.joinRawStrings(objects))

    /**
     * Removes all objects in the editor with the given group
     * @throws WSLiveEditorException if any error happens during the execution
     */
    fun removeObjects(group: UInt): Boolean =
        syncCall { continuation, client ->
            client.removeObjects(group) {
                continuation.resume(it)
            }
        }

    /**
     * Removes all objects in the editor with the given group
     * @throws WSLiveEditorException if any error happens during the execution
     */
    @JvmName("removeObjectsJava")
    fun removeObjects(group: Int): Boolean =
        this.removeObjects(group.toUInt())

    /**
     * Gets the currently opened level's raw string
     * @throws WSLiveEditorException if any error happens during the execution
     */
    fun getLevelString(): String =
        syncCall { continuation, client ->
            client.getLevelString { result ->
                continuation.resume(result)
            }
        }

    /**
     * Sets the level string of the currently opened level
     * @throws WSLiveEditorException if any error happens during the execution
     */
    fun setLevelString(levelString: String): Boolean =
        syncCall { continuation, client ->
            client.setLevelString(levelString) {
                continuation.resume(it)
            }
        }

    override fun close() {
        // We need to do this so all the websocket threads
        // actually finish executing

        // We delegate the close impl. to the async impl.
        this.asyncClient.close()
    }
}
