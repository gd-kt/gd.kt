package exceptions

import editor.wsliveeditor.WSLiveEditorClient

/**
 * @see editor.wsliveeditor.WSLiveEditor
 * @see editor.wsliveeditor.AsyncWSLiveEditor
 */
class WSLiveEditorException: GdDotKtException {
    val response: WSLiveEditorClient.WSLiveEditorResponse?

    constructor(message: String, response: WSLiveEditorClient.WSLiveEditorResponse? = null) : super(message) {
        this.response = response
    }

    constructor(message: String, cause: Throwable, response: WSLiveEditorClient.WSLiveEditorResponse? = null) : super(message, cause) {
        this.response = response
    }
}