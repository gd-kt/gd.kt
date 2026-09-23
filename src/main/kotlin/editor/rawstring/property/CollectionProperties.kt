package editor.rawstring.property

import editor.rawstring.Id
import editor.rawstring.serializing.Serializer
import java.util.*

/**
 * @see MutableList
 */
open class ListProperty<T>(
    id: Id,
    defaultValue: MutableList<T>? = arrayListOf(),
    currentValue: MutableList<T>? = null,
    elemSerializer: Serializer<T>
) : AbstractCollectionProperty<T, MutableList<T>>(id, defaultValue, currentValue, elemSerializer) {
    override fun createEmptyCollection(): MutableList<T> = arrayListOf()

    /**
     * Returns the element at the specified index in the list
     * @throws IndexOutOfBoundsException if [index] is less than zero or greater than or equal to [Collection.size] of this list
     * @see List.get
     */
    operator fun get(index: Int): T = this.getOrCreateCollection()[index]

    /**
     * Replaces the element at the specified position in this list with the specified element.
     * @return the element previously at the specified position.
     * @throws IndexOutOfBoundsException if [index] is less than zero or greater than or equal to [Collection.size] of this list.
     * @see MutableList.set
     */
    operator fun set(index: Int, element: T) {
        this.getOrCreateCollection()[index] = element
    }

    override fun asRawString(separator: Char): String =
        this.toRawIterableStringHelper(keyValSeparator = separator)
}

/**
 * @see MutableSet
 */
open class SetProperty<T>(
    id: Id,
    defaultValue: MutableSet<T>? = mutableSetOf(),
    currentValue: MutableSet<T>? = null,
    elemSerializer: Serializer<T>
) : AbstractCollectionProperty<T, MutableSet<T>>(id, defaultValue, currentValue, elemSerializer) {
    override fun asRawString(separator: Char): String =
        this.toRawIterableStringHelper(keyValSeparator = separator)

    override fun createEmptyCollection(): MutableSet<T> = mutableSetOf()
}

/**
 * @see SequencedSet
 */
open class SequencedSetProperty<T>(
    id: Id,
    defaultValue: SequencedSet<T>? = linkedSetOf(),
    currentValue: SequencedSet<T>? = null,
    elemSerializer: Serializer<T>
) : AbstractCollectionProperty<T, SequencedSet<T>>(id, defaultValue, currentValue, elemSerializer) {
    override fun asRawString(separator: Char): String =
        this.toRawIterableStringHelper(keyValSeparator =  separator)

    override fun createEmptyCollection(): SequencedSet<T> = linkedSetOf()
}