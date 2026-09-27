package editor.objects.triggers.objects

import annotations.GDName
import editor.objects.data.Pos
import editor.objects.data.Position
import editor.objects.propertycontainers.TriggerProperties
import editor.objects.triggers.TriggerObject

/**
 * An alpha trigger allows to change the opacity of any objects linked to the [target group][targetGroup].
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 * @see ToggleTrigger
 */
class AlphaTrigger : TriggerObject {
    companion object {
        const val OBJ_ID = 1007u
    }

    val fadeTime = TriggerProperties.DURATION
    @GDName("Spawn Trigger")
    val targetGroup = TriggerProperties.TARGET_GROUP
    val opacity = TriggerProperties.OPACITY

    constructor(pos: Position) : super(OBJ_ID, pos)
    constructor(x: Float, y: Float) : super(OBJ_ID, x, y)

    constructor(pos: Position, targetGroup: UInt, opacity: Float, fadeTime: Float) : this(pos) {
        this.targetGroup.value = targetGroup
        this.opacity.value = opacity
        this.fadeTime.value = fadeTime
    }
    constructor(x: Float, y: Float, targetGroup: UInt, opacity: Float, fadeTime: Float) : this(Pos(x, y), targetGroup, opacity, fadeTime)
}