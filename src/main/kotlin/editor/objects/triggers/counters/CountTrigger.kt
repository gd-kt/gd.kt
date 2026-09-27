package editor.objects.triggers.counters

import editor.objects.data.Pos
import editor.objects.data.Position
import editor.objects.propertycontainers.TriggerProperties
import editor.objects.triggers.TriggerObject
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.BoolProperty
import editor.rawstring.property.IntProperty
import editor.rawstring.property.UIntProperty

/**
 * A count trigger allows to check the equality of two counter labels.
 *
 * By default, it only triggers the [targetGroup] once, only after the equality was met. But this can be configured
 * via the [multiActivate] property.
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 * @see CountTrigger
 */
class CountTrigger : TriggerObject {
    companion object {
        const val OBJ_ID = 1611u
    }

    val itemID = UIntProperty(80.id)
    val targetGroup = TriggerProperties.TARGET_GROUP
    val targetCount = IntProperty(77.id)
    val activateGroup = TriggerProperties.ACTIVATE_GROUP
    /**
     * Allows for this trigger to activate more than once
     */
    val multiActivate = BoolProperty(104.id)

    constructor(pos: Position) : super(OBJ_ID, pos)
    constructor(x: Float, y: Float) : super(OBJ_ID, x, y)

    constructor(pos: Position, itemID: UInt, targetCount: Int) : this(pos) {
        this.itemID.value = itemID
        this.targetCount.value = targetCount
    }
    constructor(x: Float, y: Float, itemID: UInt, targetCount: Int) : this(Pos(x, y), itemID, targetCount)
}
