package editor.objects.triggers

import editor.objects.data.Pos
import editor.objects.data.Position
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.EnumProperty
import editor.rawstring.property.GdEnum
import editor.rawstring.property.MutableConditionalProperty
import editor.rawstring.property.UIntProperty
import editor.rawstring.serializing.Serializer
import editor.rawstring.serializing.Serializers

/**
 * A pickup triggers allows for the modification of a counter.
 * A counter is like a global variable that can **only store an integer**.
 *
 * Some basic operations can be done on a counter:
 * - Additions
 * - Divisions
 * - Multiplications
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 */
class PickupTrigger : TriggerObject {
    companion object {
        const val OBJ_ID = 1817u
    }

    val itemID = UIntProperty(80.id)
    val operationType = EnumProperty(88.id, Serializer.enum(OperationType.entries),  defaultValue = OperationType.ADDITION)
    /**
     * Only is compatible with [operationType] == [OperationType.ADDITION]
     */
    val count = MutableConditionalProperty(77.id, defaultValue = 0, dependantOn = operationType, serializer = Serializers.INT) {
        it.value == OperationType.ADDITION
    }
    /**
     * Only is compatible with [operationType] == [OperationType.ADDITION]
     */
    val override = MutableConditionalProperty(139.id, defaultValue = false, dependantOn = operationType, serializer = Serializers.BOOLEAN) {
        it.value == OperationType.ADDITION
    }
    /**
     * Only is compatible with [operationType] **!**= [OperationType.ADDITION]
     */
    val divisionMultiplicationFactor = MutableConditionalProperty(449.id, defaultValue = 1f, dependantOn = operationType, serializer = Serializers.FLOAT) {
        it.value != OperationType.ADDITION
    }

    constructor(pos: Position) : super(OBJ_ID, pos)
    constructor(x: Float, y: Float) : super(OBJ_ID, x, y)

    constructor(pos: Position, itemID: UInt, count: Int = 0) : this(pos) {
        this.itemID.value = itemID
        this.count.value = count
    }
    constructor(x: Float, y: Float, itemID: UInt, count: Int = 0) : this(Pos(x, y), itemID, count)
}

enum class OperationType(override val value: Int) : GdEnum {
    ADDITION(0),
    MULTIPLICATION(1),
    DIVISION(2)
}

