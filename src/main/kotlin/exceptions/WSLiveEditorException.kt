package exceptions

/**
 * @see editor.wsliveeditor.WSLiveEditor
 * @see editor.wsliveeditor.AsyncWSLiveEditor
 */
class WSLiveEditorException: GdDotKtException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}