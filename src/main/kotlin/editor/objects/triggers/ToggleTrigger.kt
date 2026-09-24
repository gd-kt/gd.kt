package editor.objects.triggers

import editor.objects.data.Pos
import editor.objects.data.Position
import editor.objects.propertycontainers.TriggerProperties
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.BoolProperty

/**
 * A toggle trigger allows for any [targeted objects][targetGroup] to be toggled on or on depending on the [activateGroup] property.
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 * @see AlphaTrigger
 */
class ToggleTrigger : TriggerObject {
    companion object {
        const val OBJ_ID = 1049u
    }

    val targetGroup = TriggerProperties.TARGET_GROUP
    val activateGroup = BoolProperty(56.id, false)

    constructor(pos: Position) : super(OBJ_ID, pos)
    constructor(x: Float, y: Float) : super(OBJ_ID, x, y)

    constructor(pos: Position, targetGroup: UInt = 0u, activateGroup: Boolean = false) : this(pos) {
        this.targetGroup.value = targetGroup
        this.activateGroup.value = activateGroup
    }
    constructor(x: Float, y: Float, targetGroup: UInt = 0u, activateGroup: Boolean = false) : this(Pos(x, y), targetGroup, activateGroup)
}