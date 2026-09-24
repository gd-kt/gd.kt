package editor.objects.triggers

import annotations.GDName
import editor.objects.data.Pos
import editor.objects.data.Position
import editor.objects.propertycontainers.TriggerProperties
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.BoolProperty
import editor.rawstring.property.ConstantProperty
import editor.rawstring.property.EnumProperty
import editor.rawstring.property.GdEnum
import editor.rawstring.property.IntProperty
import editor.rawstring.property.MutableConditionalProperty
import editor.rawstring.property.UIntProperty
import editor.rawstring.serializing.Serializer
import editor.rawstring.serializing.Serializers

abstract class AbstractMoveTrigger : TriggerObject {
    companion object {
        const val OBJ_ID = 901u
    }

    val moveTime = TriggerProperties.DURATION
    val targetGroup = TriggerProperties.TARGET_GROUP
    val easing = TriggerProperties.EASING
    val easingRate = TriggerProperties.getEasingRateProp(this.easing)
    /**
     * Decides if the units used by this trigger should be:
     *
     * |            | Small Steps off                          | Small Steps on                          |
     * |------------|------------------------------------------|-----------------------------------------|
     * | Conversion | 1 -> 3                                   | 1 -> 1                                  |
     * | Meaning    | 10*3 = 30 -> 1 grid unit  *(input = 10)* | 30*1 = 30 = 1 grid unit  *(input = 30)* |
     *
     * However, this property is a [constant one][ConstantProperty], meaning it has a **fixed value**.
     * Why is that so ? Well it's because of consistency.
     *
     * When smallStep is off, the value puts into the ui gets transformed: `10 -> 30`.
     * But this project is mainly centered around geometry's space units where 30 `space units` = 1 `grid unit`,
     * so we actually don't want this conversion to happen !
     * So by force setting this to `true` we fix this issue.
     */
    val smallStep = ConstantProperty(393.id, true, Serializers.BOOLEAN)
    val dynamicMode = BoolProperty(397.id, false)
    val silent = BoolProperty(544.id, false)

    constructor(pos: Position) : super(OBJ_ID, pos)
    constructor(x: Float, y: Float) : super(OBJ_ID, x, y)

    constructor(pos: Position, targetGroup: UInt) : this(pos) {
        this.targetGroup.value = targetGroup
    }
    constructor(x: Float, y: Float, targetGroup: UInt) : this(Pos(x, y), targetGroup)
}

/**
 * A move trigger allows to move [objects][linkedGroupID].
 * For consistency purpose, [smallStep] is a constant set to `true`. To learn why, read its KDoc !
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 * @see DirectionMoveTrigger
 * @see TargetMoveTrigger
 */
open class MoveTrigger : AbstractMoveTrigger {
    @GDName("Lock X: Player")
    val lockOnPlayerX = BoolProperty(58.id, false)
    @GDName("Lock Y: Player")
    val lockOnPlayerY = BoolProperty(59.id, false)

    @GDName("Lock X: Camera")
    val lockOnCameraX = BoolProperty(141.id, false)
    @GDName("Lock Y: Camera")
    val lockOnCameraY = BoolProperty(142.id, false)

    /**
     * The move value on the X axis of the move trigger.
     * This stores a float value because it is allowed by geometry dash, however the ui does not allow it.
     *
     * If [lockOnPlayerX] or [lockOnCameraX] is set to `true`, you must use [followingFactorX] instead.
     */
    val moveX = MutableConditionalProperty.createIndependent(28.id, 0f, serializer = Serializers.FLOAT) {
        !this.lockOnPlayerX.isSerializable() && !this.lockOnCameraX.isSerializable()
    }
    /**
     * The move value on the Y axis of the move trigger.
     * This stores a float value because it is allowed by geometry dash, however the ui does not allow it.
     *
     * If [lockOnPlayerY] or [lockOnCameraY] is set to `true`, you must use [followingFactorY] instead.
     */
    val moveY = MutableConditionalProperty.createIndependent(29.id, 0f, serializer = Serializers.FLOAT) {
        !this.lockOnPlayerY.isSerializable() && !this.lockOnCameraY.isSerializable()
    }

    /**
     * The factor at which the objects linked to this trigger will follow the [player][lockOnPlayerX] or the [camera][lockOnCameraX].
     * This only must be used with [lockOnPlayerX] or [lockOnCameraX]
     */
    @GDName("Mod X")
    val followingFactorX = MutableConditionalProperty.createIndependent(143.id, 0f, serializer = Serializers.FLOAT) {
        this.lockOnPlayerY.isSerializable() || this.lockOnCameraY.isSerializable()
    }

    /**
     * The factor at which the objects linked to this trigger will follow the [player][lockOnPlayerY] or the [camera][lockOnCameraY].
     * This only must be used with [lockOnPlayerY] or [lockOnCameraY]
     */
    @GDName("Mod Y")
    val followingFactorY = MutableConditionalProperty.createIndependent(144.id, 0f, serializer = Serializers.FLOAT) {
        this.lockOnPlayerY.isSerializable() || this.lockOnCameraY.isSerializable()
    }

    var moveVector: Pos
        get() = Pos(this.moveX.getOrThrow(), this.moveY.getOrThrow())
        set(value) {
            this.moveX.value = value.x
            this.moveY.value = value.y
        }

    var lockOnX: LockOn
        get() =
            if (this.lockOnPlayerX.value == true)
                LockOn.PLAYER
            else if (this.lockOnCameraX.value == true)
                LockOn.CAMERA
            else
                LockOn.NONE
        set(value) {
            when (value) {
                LockOn.NONE -> {
                    this.lockOnPlayerX.value = false
                    this.lockOnCameraX.value = false
                }
                LockOn.PLAYER -> {
                    this.lockOnPlayerX.value = true
                    this.lockOnCameraX.value = false
                }
                LockOn.CAMERA -> {
                    this.lockOnPlayerX.value = false
                    this.lockOnCameraX.value = true
                }
            }
        }

    var lockOnY: LockOn
        get() =
            if (this.lockOnPlayerY.value == true)
                LockOn.PLAYER
            else if (this.lockOnCameraY.value == true)
                LockOn.CAMERA
            else
                LockOn.NONE
        set(value) {
            when (value) {
                LockOn.NONE -> {
                    this.lockOnPlayerY.value = false
                    this.lockOnCameraY.value = false
                }
                LockOn.PLAYER -> {
                    this.lockOnPlayerY.value = true
                    this.lockOnCameraY.value = false
                }
                LockOn.CAMERA -> {
                    this.lockOnPlayerY.value = false
                    this.lockOnCameraY.value = true
                }
            }
        }

    constructor(pos: Position) : super(pos)
    constructor(x: Float, y: Float) : super(x, y)

    constructor(pos: Position, targetGroup: UInt) : super(pos, targetGroup)
    constructor(x: Float, y: Float, targetGroup: UInt) : super(x, y, targetGroup)
}

sealed class HasTargetMoveTrigger : MoveTrigger {
    val centerGroup = UIntProperty(395.id)
    val targetPosGroup = UIntProperty(71.id)
    val targetP1 = BoolProperty(138.id, false)
    val targetP2 = BoolProperty(200.id, false)

    var playerTarget: PlayerTarget
        get() =
            if (this.targetP1.value == true)
                PlayerTarget.PLAYER_1
            else if (this.targetP2.value == true)
                PlayerTarget.PLAYER_2
            else
                PlayerTarget.NONE
        set(value) {
            when (value) {
                PlayerTarget.NONE -> {
                    this.targetP1.value = false
                    this.targetP2.value = false
                }
                PlayerTarget.PLAYER_1 -> {
                    this.targetP1.value = true
                    this.targetP2.value = false
                }
                PlayerTarget.PLAYER_2 -> {
                    this.targetP1.value = false
                    this.targetP2.value = true
                }
            }
        }

    constructor(pos: Position) : super(pos)
    constructor(x: Float, y: Float) : super(x, y)

    constructor(pos: Position, targetGroup: UInt) : super(pos, targetGroup)
    constructor(x: Float, y: Float, targetGroup: UInt) : super(x, y, targetGroup)
}

/**
 * A **direction** move trigger allows to move [objects][linkedGroupID] in the direction of another object.
 * For consistency purpose, [smallStep] is a constant set to `true`. To learn why, read its KDoc !
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 * @see MoveTrigger
 * @see TargetMoveTrigger
 */
class DirectionMoveTrigger : HasTargetMoveTrigger {
    @GDName("Direction Mode")
    val isDirectionMode = ConstantProperty(394.id, true, Serializers.BOOLEAN)
    val distance = IntProperty(396.id)

    constructor(pos: Position) : super(pos)
    constructor(x: Float, y: Float) : super(x, y)

    constructor(pos: Position, targetGroup: UInt) : super(pos, targetGroup)
    constructor(x: Float, y: Float, targetGroup: UInt) : super(x, y, targetGroup)
}

/**
 * A **target** move trigger allows to move [objects][linkedGroupID] towards a target.
 * For consistency purpose, [smallStep] is a constant set to `true`. To learn why, read its KDoc !
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 * @see MoveTrigger
 * @see DirectionMoveTrigger
 */
class TargetMoveTrigger : HasTargetMoveTrigger {
    @GDName("Target Mode")
    val isTargetMode = ConstantProperty(100.id, true, Serializers.BOOLEAN)
    val strictAxis = EnumProperty(101.id, Serializer.enum(StrictAxis.entries))

    constructor(pos: Position) : super(pos)
    constructor(x: Float, y: Float) : super(x, y)

    constructor(pos: Position, targetGroup: UInt) : super(pos, targetGroup)
    constructor(x: Float, y: Float, targetGroup: UInt) : super(x, y, targetGroup)
}

enum class LockOn {
    NONE,
    PLAYER,
    CAMERA
}

enum class PlayerTarget {
    NONE,
    PLAYER_1,
    PLAYER_2
}

enum class StrictAxis(override val value: Int) : GdEnum {
    @GDName("X Only")
    X(0),
    @GDName("Y Only")
    Y(1);
}
