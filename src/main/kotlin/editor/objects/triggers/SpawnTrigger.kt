package editor.objects.triggers

import editor.objects.data.Pos
import editor.objects.data.Position
import editor.objects.propertycontainers.TriggerProperties
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.BoolProperty
import editor.rawstring.property.FloatProperty

/**
 * A spawn triggers allows to execute a group of triggers that's [spawn triggered][spawnTriggered].
 * This does not contain spawn remapping features.
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 */
class SpawnTrigger : TriggerObject {
    companion object {
        const val OBJ_ID = 1049u
    }

    val targetGroup = TriggerProperties.TARGET_GROUP
    val delay = FloatProperty(63.id)
    /**
     * A random delay applied to [delay] in runtime
     */
    val randomDeltaDelay = FloatProperty(556.id)
    /**
     * From Geometry Dash:
     * > `Spawn Ordered` spawns the triggers in order from left to right.
     */
    val spawnOrdered = BoolProperty(441.id)
    /**
     * From Geometry Dash:
     * > `Preview Disable` disables activation of this trigger while not
     * playtesting in the editor.
     */
    val previewDisable = BoolProperty(102.id)

    constructor(pos: Position) : super(OBJ_ID, pos)
    constructor(x: Float, y: Float) : super(OBJ_ID, x, y)

    constructor(pos: Position, targetGroup: UInt = 0u, delay: Float = 0f) : this(pos) {
        this.targetGroup.value = targetGroup
        this.delay.value = delay
    }
    constructor(x: Float, y: Float, targetGroup: UInt = 0u, delay: Float = 0f) : this(Pos(x, y), targetGroup, delay)
}