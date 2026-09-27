package editor.objects.triggers.counters

import editor.objects.data.Pos
import editor.objects.data.Position
import editor.objects.propertycontainers.TriggerProperties
import editor.objects.triggers.TriggerObject
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.EnumProperty
import editor.rawstring.property.GdEnum
import editor.rawstring.property.IntProperty
import editor.rawstring.property.UIntProperty
import editor.rawstring.serializing.Serializer

/**
 * An instant count trigger allows to do simple checks on counter labels.
 * The type of comparison can be configured through the [comparisonType] property
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 * @see InstantCountTrigger
 */
class InstantCountTrigger : TriggerObject {
    companion object {
        const val OBJ_ID = 1811u
    }

    val itemID = UIntProperty(80.id)
    val targetGroup = TriggerProperties.TARGET_GROUP
    val targetCount = IntProperty(77.id)
    val activateGroup = TriggerProperties.ACTIVATE_GROUP
    val comparisonType = EnumProperty(88.id, Serializer.enum(InstantCountComparison.entries), defaultValue = InstantCountComparison.EQUALS)

    constructor(pos: Position) : super(OBJ_ID, pos)
    constructor(x: Float, y: Float) : super(OBJ_ID, x, y)

    constructor(pos: Position, itemID: UInt, targetCount: Int, targetGroup: UInt, comparisonType: InstantCountComparison = InstantCountComparison.EQUALS) : this(pos) {
        this.itemID.value = itemID
        this.targetCount.value = targetCount
        this.targetGroup.value = targetGroup
        this.comparisonType.value = comparisonType
    }
    constructor(x: Float, y: Float, itemID: UInt, targetCount: Int, targetGroup: UInt, comparisonType: InstantCountComparison = InstantCountComparison.EQUALS) :
            this(Pos(x, y), itemID, targetCount, targetGroup, comparisonType)
}

enum class InstantCountComparison(override val value: Int) : GdEnum {
    EQUALS(0),
    LARGER(1),
    SMALLER(2)
}
