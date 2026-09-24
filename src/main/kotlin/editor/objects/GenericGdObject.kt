package editor.objects

import editor.rawstring.Id
import editor.rawstring.RawStringFactory
import editor.rawstring.RawStringable
import editor.rawstring.property.AbstractProperty
import editor.rawstring.property.PropertyDefinition

/**
 * Represents an object that can have a conversion to a "geometry dash raw string"
 * and contains [properties][AbstractProperty]
 */
interface GenericGdObject : RawStringable {
    companion object {
        /**
         * Loosely check if a raw string is valid in its form
         * @sample samples.editor.rawstring.isValidObjectStringSample
         */
        @JvmStatic
        fun isValidObjectString(rawStr: String, separator: Char = AbstractProperty.KEY_VAL_SEPARATOR): Boolean =
            rawStr.count { it == separator } % 2 == 1
    }

    val rawStringFactory: RawStringFactory

    /**
     * Get the property of this geometry dash object by its property id
     * @throws NoSuchElementException if there is no property at the given id
     */
    operator fun get(propID: Id): PropertyDefinition<*> =
        this.rawStringFactory.properties.first { it.id == propID }

    override fun asRawString(): String =
        this.rawStringFactory.asRawString()

// Old function, removed because of type unsafety
// (you can more easily assign the wrong type to the value var, yes, you can assign the WRONG TYPE)
//    /**
//     * Sets the property of this geometry dash object by its property id
//     * @throws ClassCastException if [T] is the invalid type for the corresponding property
//     * @throws NullPointerException if there is no property at the given id
//     */
//    operator fun <T> set(propID: UInt, value: T)
}
