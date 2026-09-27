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
 * For example, this example will do the comparison: `itemID 16 >= abs(floor(7.35))` and spawn the group `9` on success:
 *
 * ```kt
 * ItemCompareTrigger(0f, 0f).apply {
 *     comparisonType.value = ItemCompareComparisonType.GREATER_THAN_EQUAL
 *     succeededSpawnID.value = 9u
 *
 *     firstItemID.value = 16u
 *
 *     secondTermFactor.value = 7.35f
 *     secondTermAbsNeg.value = ItemTriggerAbsNeg.ABS
 *     secondTermRoundingType.value = ItemTriggerRoundingType.FLOOR
 * }
 * ```
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 * @see ItemEditTrigger
 */
class ItemCompareTrigger : TriggerObject {
    companion object {
        const val OBJ_ID = 3620u
    }

    val firstTermType = EnumProperty(476.id, Serializer.enum(ItemTriggerTermType.entries), defaultValue = ItemTriggerTermType.UNSET, currentValue = ItemTriggerTermType.ITEM)
    val secondTermType = EnumProperty(477.id, Serializer.enum(ItemTriggerTermType.entries), defaultValue = ItemTriggerTermType.UNSET, currentValue = ItemTriggerTermType.ITEM)

    @GDName("ItemID1")
    val firstItemID = MutableConditionalProperty(80.id, defaultValue = 0u, serializer = Serializers.UINT, dependantOn = firstTermType) { prop ->
        prop.value == ItemTriggerTermType.ITEM || prop.value == ItemTriggerTermType.TIMER
    }
    @GDName("ItemID2")
    val secondItemID = MutableConditionalProperty(95.id, defaultValue = 0u, serializer = Serializers.UINT, dependantOn = firstTermType) { prop ->
        prop.value == ItemTriggerTermType.ITEM || prop.value == ItemTriggerTermType.TIMER
    }

    @GDName("Mod1")
    val firstTermFactor = FloatProperty(479.id, currentValue = 1f)
    @GDName("Mod2")
    val secondTermFactor = FloatProperty(483.id, currentValue = 1f)

    /**
     * The group id to spawn if the check **succeeded**
     */
    @GDName("True ID")
    val succeededSpawnID = TriggerProperties.TARGET_GROUP
    /**
     * The group id to spawn if the check **failed**
     */
    @GDName("False ID")
    val failedSpawnID = TriggerProperties.getGroupProperty(71.id)

    /**
     * A random number applied to the output at runtime
     */
    val randomDeltaOutput = FloatProperty(484.id)

    val firstTermOperator = EnumProperty(480.id, Serializer.enum(ItemTriggerOperatorType.entries), defaultValue = null, currentValue = ItemTriggerOperatorType.MULTIPLICATION)
    val secondTermOperator = EnumProperty(481.id, Serializer.enum(ItemTriggerOperatorType.entries), defaultValue = null, currentValue = ItemTriggerOperatorType.MULTIPLICATION)

    val comparisonType = EnumProperty(482.id, Serializer.enum(ItemCompareComparisonType.entries), defaultValue = ItemCompareComparisonType.EQUAL)

    val firstTermAbsNeg = EnumProperty(578.id, Serializer.enum(ItemTriggerAbsNeg.entries), defaultValue = ItemTriggerAbsNeg.NONE)
    val secondTermAbsNeg = EnumProperty(579.id, Serializer.enum(ItemTriggerAbsNeg.entries), defaultValue = ItemTriggerAbsNeg.NONE)

    val firstTermRoundingType = EnumProperty(485.id, Serializer.enum(ItemTriggerRoundingType.entries), defaultValue = ItemTriggerRoundingType.NONE)
    val secondTermRoundingType = EnumProperty(486.id, Serializer.enum(ItemTriggerRoundingType.entries), defaultValue = ItemTriggerRoundingType.NONE)

    constructor(pos: Position) : super(OBJ_ID, pos)
    constructor(x: Float, y: Float) : super(OBJ_ID, x, y)
}

enum class ItemCompareComparisonType(override val value: Int) : GdEnum {
    EQUAL(0),
    GREATER_THAN(1),
    GREATER_THAN_EQUAL(2),
    LESS_THAN(3),
    LESS_THAN_EQUAL(4),
    NOT_EQUAL(5)
}
