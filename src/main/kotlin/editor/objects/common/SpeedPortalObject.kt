package editor.objects.common

import editor.objects.ComplexObject
import editor.objects.data.Position
import editor.objects.propertycontainers.TriggerProperties

/**
 * A speed portal object is an object that changes the player's speed when touched.
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 * @see Speed
 */
class SpeedPortalObject : ComplexObject {
    val multiActivate = TriggerProperties.MULTI_TRIGGERED

    var speed: Speed
        get() {
            Speed.entries.forEach {
                if (this.objID.value == it.objID)
                    return it
            }

            throw NullPointerException("Object's objID (= ${this.objID.value}) is not a correct object ID for a speed portal")
        }
        set(value) {
            this.objID.value = value.objID
        }

    constructor(speed: Speed, pos: Position) : super(speed.objID, pos)
    constructor(speed: Speed, x: Float, y: Float) : super(speed.objID, x, y)
}

enum class Speed(val speedMultiplier: Float, val objID: UInt) {
    /**
     * The orange speed portal. 0.5x normal speed.
     */
    SLOW(0.5f, 200u),
    /**
     * The blue speed portal. 1x normal speed.
     */
    NORMAL(1f, 201u),
    /**
     * The green speed portal. 2x normal speed.
     */
    FAST(2f, 202u),
    /**
     * The orange speed portal. 3x normal speed.
     */
    VERY_FAST(3f, 203u),
    /**
     * The red speed portal. 4x normal speed.
     */
    ULTRA_FAST(4f, 1334u)
}
