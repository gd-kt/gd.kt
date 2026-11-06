package editor.objects.triggers.counters

import editor.rawstring.property.GdEnum

enum class ItemTriggerTermType(override val value: Int) : GdEnum {
    UNSET(0),
    ITEM(1),
    TIMER(2),
    POINTS(3),
    TIME(4),
    ATTEMPTS(5)
}

enum class ItemTriggerOperatorType(override val value: Int) : GdEnum {
    ADDITION(ItemEditOperatorType.ADDITION_ASSIGN.value),
    SUBSTRACTION(ItemEditOperatorType.SUBSTRACTION_ASSIGN.value),
    MULTIPLICATION(ItemEditOperatorType.MULTIPLICATION_ASSIGN.value),
    DIVISION(ItemEditOperatorType.DIVISION_ASSIGN.value)
}

enum class ItemTriggerAbsNeg(override val value: Int) : GdEnum {
    NONE(0),
    ABS(1),
    NEGATE(2);

    companion object {
        @Deprecated(
            message = "NEGATIVE has been renamed to NEGATE",
            replaceWith = ReplaceWith("NEGATE")
        )
        inline val NEGATIVE
            get() = NEGATE
    }
}

enum class ItemTriggerRoundingType(override val value: Int) : GdEnum {
    NONE(0),
    ROUND(1),
    FLOOR(2),
    CEIL(3)
}