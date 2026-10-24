package exceptions

import editor.wsliveeditor.AbstractWSLiveEditorClient

/**
 * @see editor.wsliveeditor.WSLiveEditor
 * @see editor.wsliveeditor.AsyncWSLiveEditor
 */
class WSLiveEditorException: GdDotKtException {
    val response: AbstractWSLiveEditorClient.WSLiveEditorResponse?

    constructor(message: String, response: AbstractWSLiveEditorClient.WSLiveEditorResponse? = null) : super(message) {
        this.response = response
    }

    constructor(message: String, cause: Throwable, response: AbstractWSLiveEditorClient.WSLiveEditorResponse? = null) : super(message, cause) {
        this.response = response
    }
}