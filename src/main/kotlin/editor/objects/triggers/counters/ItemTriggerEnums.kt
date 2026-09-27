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
    NEGATIVE(2)
}

enum class ItemTriggerRoundingType(override val value: Int) : GdEnum {
    NONE(0),
    ROUND(1),
    FLOOR(2),
    CEIL(3)
}