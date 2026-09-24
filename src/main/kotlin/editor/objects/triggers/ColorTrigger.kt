package editor.objects.triggers

import annotations.GDName
import editor.objects.data.Pos
import editor.objects.data.Position
import editor.objects.propertycontainers.TriggerProperties
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.BoolProperty
import editor.rawstring.property.UByteProperty
import editor.rawstring.property.UIntProperty
import java.awt.Color

/**
 * A color trigger allows to change the color of any objects / color channels linked to the [color channel ID][colorChannel].
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 * @see ToggleTrigger
 */
class ColorTrigger : TriggerObject {
    companion object {
        const val OBJ_ID = 889u
    }

    val fadeTime = TriggerProperties.DURATION
    val red = UByteProperty(7.id)
    val green = UByteProperty(8.id)
    val blue = UByteProperty(9.id)
    val opacity = TriggerProperties.OPACITY
    val blending = BoolProperty(17.id, false)
    val playerColor1 = BoolProperty(15.id, false)
    val playerColor2 = BoolProperty(16.id, false)
    @GDName("Color ID")
    val colorChannel = UIntProperty(23.id)
    @GDName("Channel ID")
    val copiedColorChannel = UIntProperty(50.id)
    val legacyHsv = BoolProperty(210.id, false)
    val hsv = TriggerProperties.HSV
    val copyOpacity = BoolProperty(60.id, false)

    var playerColor: PlayerColor
        get() =
            if (this.playerColor1.value == true)
                PlayerColor.PLAYER_1
            else if (this.playerColor2.value == true)
                PlayerColor.PLAYER_2
            else
                PlayerColor.NONE
        set(value) {
            when (value) {
                PlayerColor.NONE -> {
                    this.playerColor1.value = false
                    this.playerColor2.value = false
                }
                PlayerColor.PLAYER_1 -> {
                    this.playerColor1.value = true
                    this.playerColor2.value = false
                }
                else -> {
                    this.playerColor1.value = false
                    this.playerColor2.value = true
                }
            }
        }

    var color: Color
        get() = Color(
            this.red.getOrThrow().toInt(),
            this.green.getOrThrow().toInt(),
            this.blue.getOrThrow().toInt()
        )
        set(value) {
            this.red.value = value.red.toUByte()
            this.green.value = value.green.toUByte()
            this.blue.value = value.blue.toUByte()
        }

    constructor(pos: Position) : super(OBJ_ID, pos)
    constructor(x: Float, y: Float) : super(OBJ_ID, x, y)

    constructor(pos: Position, color: Color, colorChannel: UInt) : this(pos) {
        this.color = color
        this.colorChannel.value = colorChannel
    }
    constructor(x: Float, y: Float, color: Color, colorChannel: UInt) : this(Pos(x, y), color, colorChannel)
}

enum class PlayerColor {
    NONE,
    PLAYER_1,
    PLAYER_2
}
