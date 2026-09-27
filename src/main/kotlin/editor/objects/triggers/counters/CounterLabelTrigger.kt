package editor.objects.triggers.counters

import editor.objects.data.Pos
import editor.objects.data.Position
import editor.objects.triggers.TriggerObject
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.BoolProperty
import editor.rawstring.property.EnumProperty
import editor.rawstring.property.GdEnum
import editor.rawstring.property.UIntProperty
import editor.rawstring.serializing.Serializer

/**
 * A counter label allows to visually see the content of an item ID, points, elapsed time or attempts
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 */
class CounterLabelTrigger : TriggerObject {
    companion object {
        const val OBJ_ID = 1615u
    }

    val itemID = UIntProperty(80.id)
    val labelType = EnumProperty(390.id, Serializer.enum(LabelType.entries), defaultValue = LabelType.UNSET)
    val secondsOnly = BoolProperty(389.id)
    val align = EnumProperty(391.id, Serializer.enum(CounterAlignment.entries), defaultValue = CounterAlignment.NONE)
    val timeCounter = BoolProperty(466.id)

    constructor(pos: Position) : super(OBJ_ID, pos)
    constructor(x: Float, y: Float) : super(OBJ_ID, x, y)

    constructor(pos: Position, itemID: UInt) : this(pos) {
        this.itemID.value = itemID
    }
    constructor(x: Float, y: Float, itemID: UInt) : this(Pos(x, y), itemID)
}

enum class LabelType(override val value: Int) : GdEnum {
    UNSET(0),
    MAIN_TIME(-1),
    POINTS(-2),
    ATTEMPTS(-3)
}

enum class CounterAlignment(override val value: Int) : GdEnum {
    NONE(0),
    LEFT(1),
    RIGHT(2)
}
