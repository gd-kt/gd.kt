package editor.rawstring.property

import editor.rawstring.Id
import editor.rawstring.serializing.Serializable
import editor.rawstring.serializing.Serializer
import editor.rawstring.RawStringable
import utils.nonNull
import utils.quote
import kotlin.reflect.KProperty

/**
 * The base interface for all properties.
 * This property is **immutable**. See [MutableProperty] for a mutable property interface
 * @see MutableProperty
 */
interface PropertyDefinition<T> : RawStringable {
    companion object {
        /**
         * Creates the result for a [toString] result,
         * for a [property][PropertyDefinition]
         */
        fun createToString(prop: PropertyDefinition<*>): String {
            val valueStr = if (prop.value is CharSequence)
                prop.value.toString().quote()
            else
                prop.value.toString()

            return if (prop is AbstractProperty<*>) {
                "${this::class.simpleName!!}(id = ${prop.id}, defaultValue = ${prop.defaultValue}, value = $valueStr)"
            } else {
                "${this::class.simpleName!!}(id = ${prop.id}, value = $valueStr)"
            }
        }
    }

    val id: Id
    val serializer: Serializer<T>

    /**
     * The current value of this property.
     *
     * If you are overriding this, setting this as a `var` is allowed and
     * expected behavior
     */
    val value: T?

    /**
     * Checks if this property needs to be serialized
     * @sample samples.editor.rawstring.property.isSerializableSample
     */
    fun isSerializable(): Boolean

    /**
     * Turns this property into the raw string understandable by geometry dash.
     * @return the raw string in the format `id,value`
     */
    override fun asRawString(): String =
        asRawString(AbstractProperty.KEY_VAL_SEPARATOR)

    /**
     * Turns this property into the raw string understandable by geometry dash.
     *
     * If you are implementing [AbstractProperty], use [AbstractProperty.toRawStringHelper] to make a raw string
     * @return the raw string in the format `id<separator>value`
     */
    fun asRawString(separator: Char = AbstractProperty.KEY_VAL_SEPARATOR): String

    /**
     * Returns the property's [value] or throw if it's `null`.
     * This is only useful if the default value is nullable and is suggested
     * to be used with [collection properties][AbstractCollectionProperty].
     *
     * `this.value!!` can also be used but this has a proper error message.
     * @return the property's [value] or throw if it's `null`
     * @throws NullPointerException if the property's [value] is `null`
     */
    fun getOrThrow(): T =
        nonNull("${this::class.simpleName}'s value is null when it was expected to be non null") { this.value }

    /**
     * Returns the property's [value] or [other] if it's `null`.
     * This is only useful if the default value is nullable
     * @return the property's [value] or [other] if it's `null`
     */
    fun getOrElse(other: T): T =
        if (this.value == null)
            other
        else
            this.value!!

    /**
     * Returns the property's [value] or [other] if it's `null`.
     * This is only useful if the default value is nullable
     * @return the property's [value] or [other] if it's `null`
     */
    fun getOrNullableElse(other: T?): T? =
        if (this.value == null)
            other
        else
            this.value!!

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? =
        this.value
}

/**
 * Compares this property's value and [other]. An exception is throw if [value] is `null`.
 * @throws NullPointerException if [value] is `null
 * @return the comparison between [value] and [other]
 * @see Comparable.compareTo
 * @see PropertyDefinition.compareOrElse
 */
fun <T : Comparable<T>> PropertyDefinition<T>.compareOrThrow(other: T): Int =
    this.getOrThrow().compareTo(other)

/**
 * Compares this property's value and [other]. [default] is returned if [value] is `null`.
 * @return the comparison between [value] and [other], or [default] if if [value] is `null`
 * @see Comparable.compareTo
 * @see PropertyDefinition.compareOrThrow
 */
fun <T : Comparable<T>> PropertyDefinition<T>.compareOrElse(other: T, default: Int): Int =
    if (this.value == null)
        default
    else
        this.compareOrThrow(other)

/**
 * Represents a property that is mutable
 */
interface MutableProperty<T> : PropertyDefinition<T> {
    /**
     * The current value of the property
     */
    override var value: T?

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        this.value = value
    }
}

/**
 * The base class for all properties
 * @property id the id of the property. See the [Id] class on how to create an instance
 * @property defaultValue the default value if the property, this affects if it will be serialized or not
 * @property currentValue the value to set by default. It defaults to the property value
 */
abstract class AbstractProperty<T>(final override val id: Id, open val defaultValue: T? = null, protected var currentValue: T? = null) : MutableProperty<T> {
    companion object {
        const val KEY_VAL_SEPARATOR: Char = ','

        fun rawStringToPair(propRawString: String): Pair<String, String> {
            val data = propRawString.split(KEY_VAL_SEPARATOR, limit = 2)
            return Pair(data[0], data[1])
        }

        /**
         * Turns a raw string into a pair of format `propID: propValue` where `propID`
         * is a parsed [Id]
         * @throws IllegalArgumentException if the prop's [Id] is below `0` (exclusive, so `< 0`)
         * @see Id.ofUnknown
         */
        fun rawStringToPairID(propRawString: String, separator: Char = KEY_VAL_SEPARATOR): Pair<Id, String> {
            val data = propRawString.split(separator, limit = 2)
            return Pair(Id.ofUnknown(data[0]), data[1])
        }
    }

    /**
     * The current value of this property.
     * The actual value is stored internally and is not meant to be accessed.
     *
     * If set to `null`, the [defaultValue] will be used instead *(see [resetValue])*
     *
     * Do note that this variable **may be dangerous to mutate** (changing the variable is not tho !!) (eg: adding elements to a collection)
     * because this variable returns the [defaultValue] if the property's [internal value][currentValue] is `null`, so
     * in that case you might modify it indirectly.
     *
     * This is why your custom properties should contain calls that do not
     * directly access [value] to prevent from modifying the default value (ex: to add an elem in a collection).
     * You can look at how this was done in [AbstractCollectionProperty] which has to in a way fight
     * against this limitation using [AbstractCollectionProperty.getOrCreateCollection].
     *
     * This is why the best practice for properties is to **contain immutable types**! (ex: primitive types)
     */
    override var value: T?
        get() {
            return if (this.currentValue == null)
                this.defaultValue
            else
                this.currentValue
        }
        set(value) { this.currentValue = value }

    /**
     * Checks if this property's [value] is equal to its [default value][defaultValue]
     */
    fun isDefaultValue(): Boolean = this.defaultValue == this.value

    /**
     * Reset this property's [value] to its [default value][defaultValue]
     */
    fun resetValue() {
        this.currentValue = this.defaultValue
    }

    /**
     * Checks if this property needs to be serialized
     * @sample samples.editor.rawstring.property.isSerializableSample
     */
    override fun isSerializable(): Boolean {
        // if there's no value, no need to serialize it into a raw string
        if (this.currentValue == null)
            return false

        // if the value is equal to the default value, no need to serialize it into a raw string
        return this.defaultValue != this.currentValue
    }

    /**
     * Helper to make raw strings
     * @param value the value of the property, possibly put in a string format if needed.
     *              This will always get converted into a [String]
     * @param suffix the suffix to append
     * @param suffixMode the mode deciding how to append the suffix
     * @return the raw string of this property in the format `id,value`.
     *         However, an empty string can be returned if [value] is `null` or if
     *         this property is [not serializable][isSerializable]
     * @see RawStringable
     * @see asRawString
     */
    protected open fun toRawStringHelper(value: Any? = this.value, separator: Char = KEY_VAL_SEPARATOR, suffix: String = "", suffixMode: SuffixMode = SuffixMode.DEFAULT): String {
        return if (this.isSerializable() && value != null) {
            this.id.getID() + separator + value.toString() + suffixMode.getString(this, suffix)
        } else {
            ""
        }
    }

    /**
     * Helper to make raw strings
     * @param suffix the suffix to append
     * @param suffixMode the mode deciding how to append the suffix
     * @param valueGetter the getter used to obtain the value of the property, possibly put in a string format if needed.
     *                    This will always get converted into a [String]
     * @return the raw string of this property in the format `id,value`.
     *         However, an empty string can be returned if [value] is `null` or if
     *         this property is [not serializable][isSerializable]
     * @see RawStringable
     * @see asRawString
     */
    protected open fun toRawStringHelper(separator: Char = KEY_VAL_SEPARATOR, suffix: String = "", suffixMode: SuffixMode = SuffixMode.DEFAULT, valueGetter: (T) -> Any): String {
        return if (this.isSerializable()) {
            val value = valueGetter(this.getOrThrow())
            this.id.getID() + separator + value.toString() + suffixMode.getString(this, suffix)
        } else {
            ""
        }
    }

    /**
     * Helper to make raw strings
     * @param serializer the serializer used to serialize the value into a raw string
     * @param suffix the suffix to append
     * @param suffixMode the mode deciding how to append the suffix
     * @return the raw string of this property in the format `id,value`.
     *         However, an empty string can be returned if [value] is `null` or if
     *         this property is [not serializable][isSerializable]
     * @see RawStringable
     * @see asRawString
     */
    protected open fun toRawStringHelper(serializer: Serializable<T>, separator: Char = KEY_VAL_SEPARATOR, suffix: String = "", suffixMode: SuffixMode = SuffixMode.DEFAULT): String =
        this.toRawStringHelper(separator, suffix, suffixMode) { serializer.serialize(it) }

    override fun toString(): String =
        PropertyDefinition.createToString(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractProperty<*>) return false

        if (this.id != other.id) return false
        if (this.defaultValue != other.defaultValue) return false
        if (this.value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = this.id.hashCode()
        result = 31 * result + this.defaultValue.hashCode()
        result = 31 * result + this.value.hashCode()
        return result
    }
}

typealias CollectionCtor<T> = () -> T

/**
 * The base class for all properties backed by a [Collection]
 * @property id the id of the property. It's unsigned because no properties have negative IDs.
 *              Also in geometry dash, no id goes below 1
 * @property defaultValue the default value if the property, this affects if it will be serialized or not.
 *                        It is represented by a [MutableCollection] but **it is not advised to modify it in any way !**
 * @property currentValue the value to set by default. It defaults to the property value
 * @property elemSerializer the [serializer][Serializable] used to serialize the collection's values. See [Serializer.collectionSerializer]
 * @param T the type of the collection's content
 * @param C the collection type
 */
abstract class AbstractCollectionProperty<T, C>(
    id: Id,
    defaultValue: C? = null,
    currentValue: C? = null,
    val elemSerializer: Serializer<T>
) : AbstractProperty<C>(id, defaultValue, currentValue) where C : MutableCollection<T> {
    companion object {
        const val ELEMENT_SEPARATOR: Char = '.'
    }

    override val serializer: Serializer<C> =
        Serializer.collectionSerializer(this::createEmptyCollection, this.elemSerializer)

    protected abstract fun createEmptyCollection(): C

    /**
     * Gets the underlying collection, or creates if necessary.
     *
     * **This should be called when modifying the collection** !
     */
    fun getOrCreateCollection(): C {
        if (this.currentValue == null) {
            val collection = this.createEmptyCollection()
            this.defaultValue?.let(collection::addAll)
            this.currentValue = collection
        }

        return this.currentValue!!
    }

    override var value: C?
        get() =
            if (this.currentValue == null)
                this.defaultValue
            else
                this.currentValue
        set(value) {
            super.value = value
        }

    /**
     * Returns if the underlying collection is [empty][Collection.isEmpty]
     * @see Collection.isEmpty
     */
    fun isEmpty(): Boolean =
        this.value == null || this.value!!.isEmpty()

    /**
     * Removes all the elements from the underlying collection
     * @see MutableCollection.clear
     */
    fun clear() = this.getOrCreateCollection().clear()

    /**
     * Adds the specified element to the collection.
     * @return `true` if the element has been added, `false` if the collection does not support duplicates
     * and the element is already contained in the collection.
     * @see MutableCollection.add
    */
    fun add(element: T): Boolean = this.getOrCreateCollection().add(element)

    /**
     * Removes a single instance of the specified element from this
     * collection, if the collection contains it.
     * @return `true` if the element has been successfully removed; `false` if it was not contained in the collection.
     * @see MutableCollection.remove
     */
    fun remove(element: T): Boolean = this.getOrCreateCollection().remove(element)

    /**
     * The size of the underlying collection
     * @see Collection.size
     */
    fun size(): Int = this.getOrCreateCollection().size

    /**
     * See [toRawStringHelper] for more info
     * @see [toRawStringHelper]
     */
    protected fun toRawIterableStringHelper(
        value: Collection<T>? = this.value,
        keyValSeparator: Char = KEY_VAL_SEPARATOR,
        suffix: String = "",
        suffixMode: SuffixMode = SuffixMode.DEFAULT
    ): String {
        return if (value == null || this.isEmpty() || !this.isSerializable()) {
            ""
        } else {
            this.toRawStringHelper(
                this.serializer,
                keyValSeparator,
                suffix,
                suffixMode
            )
        }
    }
}

/**
 * The mode on how to apply the suffix in [AbstractProperty.toRawStringHelper]
 */
enum class SuffixMode {
    /**
     * Always add the suffix
     */
    ALWAYS,

    /**
     * Only add the suffix when the property is [serializable][AbstractProperty.isSerializable]
     * @see AbstractProperty.isSerializable
     */
    WHEN_SERIALIZABLE;

    companion object {
        inline val DEFAULT
            get() = WHEN_SERIALIZABLE
    }

    fun getString(prop: AbstractProperty<*>, suffix: String): String {
        return if (this == ALWAYS)
            suffix
        else if (prop.isSerializable())
            suffix
        else
            ""
    }
}