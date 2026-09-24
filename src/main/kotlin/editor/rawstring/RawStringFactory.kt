package editor.rawstring

import editor.objects.GenericGdObject
import editor.rawstring.property.AbstractProperty
import editor.rawstring.property.PropertyDefinition
import exceptions.InvalidRawStringException

/**
 * A raw string factory allows you to abstract the generation of raw string for [GenericGdObjects][GenericGdObject].
 * Internally, the **default implementation** uses reflection to look for [PropertyDefinitions][PropertyDefinition] and create the raw string from there.
 * @see DynamicRawStringFactory
 */
interface RawStringFactory {
    companion object {
        const val OBJECTS_SEPARATOR: Char = ';'

        /**
         * Creates the default implementation for a raw string factory
         */
        @JvmStatic
        fun create(parent: GenericGdObject, keyValSeparator: Char = AbstractProperty.KEY_VAL_SEPARATOR): DynamicRawStringFactory =
            RawStringFactoryImpl(parent, keyValSeparator)

        /**
         * Joins multiple objects into a larger raw string understandable by geometry dash.
         * Each entry is separated by a semicolon.
         */
        @JvmStatic
        fun joinRawStrings(objects: Collection<RawStringable>, separator: Char = OBJECTS_SEPARATOR): String =
            objects.joinToString(separator.toString()) { it.asRawString() }

        /**
         * Joins multiple objects into a larger raw string understandable by geometry dash.
         * Each entry is separated by a semicolon.
         */
        @JvmStatic
        fun joinRawStrings(vararg objects: RawStringable, separator: Char = OBJECTS_SEPARATOR): String =
            joinRawStrings(objects = listOf(*objects), separator)

        /**
         * Joins multiple objects' raw strings into a larger raw string understandable by geometry dash.
         * Each entry is separated by a semicolon.
         */
        @JvmStatic
        @JvmName("joinRawStringsFromCharSequence")
        fun joinRawStrings(objects: Collection<CharSequence>, separator: Char = OBJECTS_SEPARATOR): String =
            objects.joinToString(separator.toString())

        /**
         * Joins multiple objects' raw strings into a larger raw string understandable by geometry dash.
         * Each entry is separated by a semicolon.
         */
        @JvmStatic
        @JvmName("joinRawStringsFromCharSequence")
        fun joinRawStrings(vararg objects: CharSequence, separator: Char = OBJECTS_SEPARATOR): String =
            joinRawStrings(objects = listOf(*objects), separator)

        /**
         * Turns a raw string into a [Map] in the format `propID: value`
         * @param rawString the raw string to turn into a map
         * @param separator the separator used to separate the raw string (ex, in `1,10,2,20,3,30` the separator is `,`)
         * @return the map output where keys are [ids][Id] and the values the prop's value
         * @throws InvalidRawStringException if the raw string is invalid *(see [GenericGdObject.isValidObjectString])*
         * @throws IllegalArgumentException if any of the parsed [ids][Id] are below `0` (exclusive, so `< 0`)
         */
        @JvmStatic
        fun rawStringToMap(rawString: String, separator: Char = AbstractProperty.KEY_VAL_SEPARATOR): Map<Id, String> {
            return if (GenericGdObject.isValidObjectString(rawString, separator)) {
                val rawStrAsPairs = rawString.split(separator).chunked(2) {
                    AbstractProperty.rawStringToPairID(it.joinToString(separator.toString()), separator)
                }.toTypedArray()

                mapOf(*rawStrAsPairs)
            } else {
                throw InvalidRawStringException(rawString)
            }
        }

        /**
         * Checks the equality of 2 possible raw strings. If any of the raw strings are malformed `false` is returned
         * @sample samples.editor.rawstring.areRawStringEqualsSample
         */
        @JvmStatic
        fun areRawStringEquals(a: String, b: String, separator: Char = AbstractProperty.KEY_VAL_SEPARATOR): Boolean {
            return try {
                rawStringToMap(a, separator) == rawStringToMap(b, separator)
            } catch (_: InvalidRawStringException) {
                false
            } catch (_: IllegalArgumentException) {
                false
            }
        }

        @JvmStatic
        fun createRawString(properties: Collection<PropertyDefinition<*>>, separator: Char = AbstractProperty.KEY_VAL_SEPARATOR) =
            properties.joinToString(separator.toString()) {
                it.asRawString()
            }
    }

    /**
     * The properties of this factory's object.
     *
     * On the **default impl.** they are cached per-instance and are only cached when this var's getter is called.
     */
    val properties: Collection<PropertyDefinition<*>>
    val keyValSeparator: Char

    /**
     * Get the raw string of this factory's linked obj by concatenating all
     * properties' raw strings
     * @return the raw string of this factory's linked obj
     * @see PropertyDefinition.asRawString
     */
    fun asRawString(): String =
        createRawString(this.getSerializableProperties(), this.keyValSeparator)

    fun getSerializableProperties(): List<PropertyDefinition<*>> =
        this.properties
            .stream()
            .filter { it.isSerializable() }
            .toList()

    /**
     * Gets all the **serializable** properties of this factory's parent in a map
     * in the format `propID: prop`
     * @return the properties in a [Map]
     */
    fun asMap(): Map<Id, PropertyDefinition<*>>
}

/**
 * Gets all the **serializable** properties of this factory's parent in a map
 * in the format `propID: prop`
 * @return the properties in a [Map]
 * @throws NullPointerException if **ANY** of the [numerical ids][Id.numericalID] is `null`
 */
fun RawStringFactory.asIntMap(): Map<UInt, PropertyDefinition<*>> =
    this.asMap().mapKeys { it.key.getNumericalIdStrict() }

/**
 * Gets all the **serializable** properties of this factory's parent in a map
 * in the format `propID: prop`
 * @return the properties in a [Map]
 * @throws NullPointerException if **ANY** of the [string ids][Id.stringID] is `null`
 */
fun RawStringFactory.asStringMap(): Map<String, PropertyDefinition<*>> =
    this.asMap().mapKeys { it.key.getStringIdStrict() }

/**
 * Gets all the **serializable** properties of this factory's parent in a map
 * in the format `propID: propRawString`
 * @return the properties in a [Map]
 */
fun RawStringFactory.asRawStringMap(): Map<Id, String> =
    this.asMap().mapValues { it.value.asRawString() }

/**
 * Gets all the **serializable** properties of this factory's parent in a map
 * in the format `propID: propRawString`
 * @return the properties in a [Map]
 * @throws NullPointerException if **ANY** of the [numerical ids][Id.numericalID] is `null`
 */
fun RawStringFactory.asRawStringIntMap(): Map<UInt, String> =
    this.asIntMap().mapValues { it.value.asRawString() }

/**
 * Gets all the **serializable** properties of this factory's parent in a map
 * in the format `propID: propRawString`
 * @return the properties in a [Map]
 * @throws NullPointerException if **ANY** of the [string ids][Id.stringID] is `null`
 */
fun RawStringFactory.asRawStringStringMap(): Map<String, String> =
    this.asStringMap().mapValues { it.value.asRawString() }
