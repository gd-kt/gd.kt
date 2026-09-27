package editor.objects.triggers.counters

import annotations.GDName
import editor.objects.data.Position
import editor.objects.propertycontainers.TriggerProperties
import editor.objects.triggers.TriggerObject
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.EnumProperty
import editor.rawstring.property.FloatProperty
import editor.rawstring.property.GdEnum
import editor.rawstring.property.MutableConditionalProperty
import editor.rawstring.serializing.Serializer
import editor.rawstring.serializing.Serializers

/**
 * An item compare trigger allows you to do **complex comparisons** between numbers and counters.
 * Inside the trigger, float arithmetic is allowed, but the output will always get floored
 * back to an int unless configured with [firstTermRoundingType] or [secondTermRoundingType].
 *
 * For example, this example will do the calculation: `itemID 16 += (itemID 2) / 8`:
 *
 * ```kt
 * ItemEditTrigger(0f, 0f).apply {
 *     resultItemID.value = 16u
 *     assignOperator.value = ItemEditOperatorType.ADDITION_ASSIGN
 *
 *     secondTermOperator.value = ItemTriggerOperatorType.DIVISION
 *     secondItemID.value = 2u
 *     secondTermFactor.value = 8f
 * }
 * ```
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 * @see ItemCompareTrigger
 */
class ItemEditTrigger : TriggerObject {
    companion object {
        const val OBJ_ID = 3619u
    }

    val firstTermType = EnumProperty(476.id, Serializer.enum(ItemTriggerTermType.entries), defaultValue = ItemTriggerTermType.UNSET, currentValue = ItemTriggerTermType.ITEM)
    val secondTermType = EnumProperty(477.id, Serializer.enum(ItemTriggerTermType.entries), defaultValue = ItemTriggerTermType.UNSET, currentValue = ItemTriggerTermType.ITEM)
    val targetType = EnumProperty(478.id, Serializer.enum(ItemEditTargetType.entries), defaultValue = ItemEditTargetType.ITEM)

    /**
     * The itemID that will store the output
     */
    @GDName("Target ItemID")
    val resultItemID = TriggerProperties.TARGET_GROUP

    @GDName("ItemID1")
    val firstItemID = MutableConditionalProperty(80.id, defaultValue = 0u, serializer = Serializers.UINT, dependantOn = firstTermType) { prop ->
        prop.value == ItemTriggerTermType.ITEM || prop.value == ItemTriggerTermType.TIMER
    }
    @GDName("ItemID2")
    val secondItemID = MutableConditionalProperty(95.id, defaultValue = 0u, serializer = Serializers.UINT, dependantOn = firstTermType) { prop ->
        prop.value == ItemTriggerTermType.ITEM || prop.value == ItemTriggerTermType.TIMER
    }

    @GDName("Mod")
    val secondTermFactor = FloatProperty(479.id, currentValue = 1f)

    val assignOperator = EnumProperty(480.id, Serializer.enum(ItemEditOperatorType.entries), defaultValue = ItemEditOperatorType.EQUALS)
    val firstTermOperator = EnumProperty(481.id, Serializer.enum(ItemTriggerOperatorType.entries), defaultValue = null, currentValue = ItemTriggerOperatorType.ADDITION)
    val secondTermOperator = EnumProperty(482.id, Serializer.enum(ItemTriggerOperatorType.entries), defaultValue = null, currentValue = ItemTriggerOperatorType.MULTIPLICATION)

    val firstTermAbsNeg = EnumProperty(578.id, Serializer.enum(ItemTriggerAbsNeg.entries), defaultValue = ItemTriggerAbsNeg.NONE)
    val secondTermAbsNeg = EnumProperty(579.id, Serializer.enum(ItemTriggerAbsNeg.entries), defaultValue = ItemTriggerAbsNeg.NONE)

    val firstTermRoundingType = EnumProperty(485.id, Serializer.enum(ItemTriggerRoundingType.entries), defaultValue = ItemTriggerRoundingType.NONE)
    val secondTermRoundingType = EnumProperty(486.id, Serializer.enum(ItemTriggerRoundingType.entries), defaultValue = ItemTriggerRoundingType.NONE)

    constructor(pos: Position) : super(OBJ_ID, pos)
    constructor(x: Float, y: Float) : super(OBJ_ID, x, y)
}

enum class ItemEditTargetType(override val value: Int) : GdEnum {
    ITEM(ItemTriggerTermType.ITEM.value),
    TIMER(ItemTriggerTermType.TIMER.value),
    POINTS(ItemTriggerTermType.POINTS.value)
}

enum class ItemEditOperatorType(override val value: Int) : GdEnum {
    /**
     * This is equal to the `==` operator, or mathematically equal to `a = b`
     */
    EQUALS(0),
    /**
     * This is equal to the `+=` operator, or mathematically equal to `a = a + b`
     */
    ADDITION_ASSIGN(1),
    /**
     * This is equal to the `-=` operator, or mathematically equal to `a = a - b`
     */
    SUBSTRACTION_ASSIGN(2),
    /**
     * This is equal to the `*=` operator, or mathematically equal to `a = a * b`
     */
    MULTIPLICATION_ASSIGN(3),
    /**
     * This is equal to the `/=` operator, or mathematically equal to `a = a / b`
     */
    DIVISION_ASSIGN(4)
}

