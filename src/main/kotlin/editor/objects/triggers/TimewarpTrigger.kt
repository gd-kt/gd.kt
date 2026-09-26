package editor.objects.triggers

import editor.objects.data.Pos
import editor.objects.data.Position
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.FloatProperty

/**
 * A time warp trigger allows to modify how fast the level is running
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 */
class TimewarpTrigger : TriggerObject {
    companion object {
        const val OBJ_ID = 1935u
    }

    val timeMod = FloatProperty.ranged(120.id, 0.1f..2f, defaultValue = 1f)

    constructor(pos: Position) : super(OBJ_ID, pos)
    constructor(x: Float, y: Float) : super(OBJ_ID, x, y)

    constructor(pos: Position, timeMod: Float) : this(pos) {
        this.timeMod.value = timeMod
    }
    constructor(x: Float, y: Float, timeMod: Float) : this(Pos(x, y), timeMod)
}