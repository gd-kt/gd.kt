package editor.rawstring.property

import editor.rawstring.Id
import editor.rawstring.serializing.Serializers
import java.util.SequencedSet

/**
 * Special property used to store object groups.
 * It needs to know the [groupParentsProperty], this is where "pink groups" (or "parents group") are stored.
 *
 * [value] returns both this property's stored groups and the [groupParentsProperty]'s groups.
 */
class GroupSetProperty(
    id: Id,
    val groupParentsProperty: SequencedSetProperty<UInt>,
    collectionCtor: CollectionCtor<SequencedSet<UInt>> = { linkedSetOf() },
    defaultValue: SequencedSet<UInt>? = collectionCtor(),
    currentValue: SequencedSet<UInt>? = null
) : SequencedSetProperty<UInt>(id, defaultValue =  defaultValue, currentValue = currentValue, elemSerializer = Serializers.UINT) {
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
}