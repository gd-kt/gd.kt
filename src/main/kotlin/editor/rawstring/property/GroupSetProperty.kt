package editor.rawstring.property

import editor.objects.propertycontainers.TriggerProperties
import editor.rawstring.Id
import editor.rawstring.serializing.Serializer
import java.util.SequencedSet

/**
 * A Geometry Dash group that can be interpreted
 * as "null".
 *
 * This group **cannot be linked to anything** and this is why
 * it can be qualified as a null group.
 */
const val NULL_GROUP: UInt = 0u

/**
 * Special property used to store object groups.
 * It needs to know the [groupParentsProperty], this is where "pink groups" (or "parents group") are stored.
 *
 * [value] returns both this property's stored groups and the [groupParentsProperty]'s groups.
 */
open class GroupSetProperty(
    id: Id,
    val groupParentsProperty: SequencedSetProperty<UInt>,
    defaultValue: SequencedSet<UInt>? = linkedSetOf(),
    currentValue: SequencedSet<UInt>? = null
) : SequencedSetProperty<UInt>(id, defaultValue =  defaultValue, currentValue = currentValue, elemSerializer = Serializer.clampedUInt(0u..TriggerProperties.MAX_GROUPS)) {
    override var value: SequencedSet<UInt>?
        get() {
            val linkedSet = linkedSetOf<UInt>()

            val originalValue = super.value
            if (originalValue != null)
                linkedSet.addAll(originalValue)

            val groupParents = this.groupParentsProperty.value
            if (groupParents != null)
                linkedSet.addAll(groupParents)

            return linkedSet
        }
        set(value) {
            super.value = value
        }

    // For groups, we don't need to add the group 0u (aka NULL_GROUP)
    // since it's equal to "no group"
    //
    // If the user tries to add the group 0u
    // false will be returned
    override fun add(element: UInt): Boolean =
        element != NULL_GROUP && super.add(element)
}