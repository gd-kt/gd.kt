package editor.objects

import editor.rawstring.DynamicRawStringFactory
import editor.rawstring.Id
import editor.rawstring.RawStringFactory
import editor.rawstring.property.AbstractProperty
import editor.rawstring.property.MutableProperty
import exceptions.InvalidRawStringException
import kotlin.reflect.KVisibility
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

object ObjectParser {
    /**
     * Parses any raw string into [T]. Every [mutable properties][MutableProperty] of this object
     * are filled accordingly to the raw string.
     * This chooses which parsing implementation to use
     * @param rawString the raw string to parse
     * @param toFill the instance to fill
     * @param separator the separator used to separate the raw string (ex, in `1,10,2,20,3,30` the separator is `,`)
     * @return returns the final instance, aka the same as [toFill]
     * @throws exceptions.InvalidRawStringException if the raw string is invalid *(see [GenericGdObject.isValidObjectString])*
     * @throws IllegalArgumentException if any of the parsed [ids][Id] are below `0` (exclusive, so `< 0`)
     * @see parseAny
     * @see parseGdObject
     */
    inline fun <reified T : Any> parse(rawString: String, toFill: T = T::class.createInstance(), separator: Char = AbstractProperty.KEY_VAL_SEPARATOR): T {
        return if (T::class.isSubclassOf(GenericGdObject::class))
            parseGdObject(rawString, toFill as GenericGdObject, separator) as T
        else
            parseAny(rawString, toFill, separator)
    }

    /**
     * Parses any raw string into [T]. Every [mutable properties][MutableProperty] of this object
     * are filled accordingly to the raw string.
     * To parse a [GenericGdObject], [parseGdObject] should get used instead
     * @param rawString the raw string to parse
     * @param toFill the instance to fill
     * @param separator the separator used to separate the raw string (ex, in `1,10,2,20,3,30` the separator is `,`)
     * @return returns the final instance, aka the same as [toFill]
     * @throws exceptions.InvalidRawStringException if the raw string is invalid *(see [GenericGdObject.isValidObjectString])*
     * @throws IllegalArgumentException if any of the parsed [ids][Id] are below `0` (exclusive, so `< 0`)
     * @sample samples.editor.objects.parseAnySample
     */
    inline fun <reified T : Any> parseAny(rawString: String, toFill: T = T::class.createInstance(), separator: Char = AbstractProperty.KEY_VAL_SEPARATOR): T =
        parseAnyJava(rawString, toFill, T::class.java, separator)

    /**
     * @see parseAnyJava
     */
    fun <T : Any> parseAnyJava(rawString: String, toFill: T, clazz: Class<T>, separator: Char = AbstractProperty.KEY_VAL_SEPARATOR): T {
        try {
            val rawStringMap = RawStringFactory.rawStringToMap(rawString, separator)
            clazz.kotlin.memberProperties.forEach {
                if (it.visibility == KVisibility.PUBLIC && it.returnType.isSubtypeOf(MutableProperty::class.starProjectedType)) {
                    @Suppress("UNCHECKED_CAST")
                    val prop = it.get(toFill) as MutableProperty<Any?>

                    if (rawStringMap.containsKey(prop.id)) {
                        val value = prop.serializer.parse(rawStringMap[prop.id]!!)
                        if (value != null)
                            prop.value = value
                    }
                }
            }

            return toFill
        } catch (e: Throwable) {
            throw InvalidRawStringException("Caught an error while trying to parse (any) $toFill (of type ${toFill::class.simpleName}). Separator is '$separator' with the raw string: \"$rawString\"", e)
        }
    }

    /**
     * Parses any raw string into [T]. Every [mutable properties][MutableProperty] of this object
     * are filled accordingly to the raw string. This uses the obj's [RawStringFactory] to iterate through the properties, using its implementation instead.
     * To parse something that is NOT a [GenericGdObject], [parseAny] should get used instead.
     *
     * If the [object][GenericGdObject]'s [RawStringFactory] is [dynamic][DynamicRawStringFactory], the properties not found inside
     * the object will get added in the [dynamic properties][DynamicRawStringFactory.dynamicProperties] list
     * @param rawString the raw string to parse
     * @param toFill the instance to fill
     * @param separator the separator used to separate the raw string (ex, in `1,10,2,20,3,30` the separator is `,`)
     * @return returns the final instance, aka the same as [toFill]
     * @throws exceptions.InvalidRawStringException if the raw string is invalid *(see [GenericGdObject.isValidObjectString])*
     * @throws IllegalArgumentException if any of the parsed [ids][Id] are below `0` (exclusive, so `< 0`)
     * @sample samples.editor.objects.parseGenericGdObjectSample
     */
    inline fun <reified T : GenericGdObject> parseGdObject(rawString: String, toFill: T = T::class.createInstance(), separator: Char = AbstractProperty.KEY_VAL_SEPARATOR): T =
        parseGdObjectJava(rawString, toFill, separator)

    /**
     * @see parseGdObject
     */
    fun <T : GenericGdObject> parseGdObjectJava(rawString: String, toFill: T, separator: Char = AbstractProperty.KEY_VAL_SEPARATOR): T {
        try {
            val rawStringMap = RawStringFactory.rawStringToMap(rawString, separator)
            val isDynamicRawStringFactory = toFill.rawStringFactory is DynamicRawStringFactory

            val defaultParseBehavior = { prop: MutableProperty<*> ->
                try {
                    @Suppress("UNCHECKED_CAST")
                    val castedProp = prop as MutableProperty<Any?>
                    val value = castedProp.serializer.parse(rawStringMap[castedProp.id]!!)
                    if (value != null)
                        castedProp.value = value
                } catch (e: Throwable) {
                    throw InvalidRawStringException("Couldn't successfully parse prop $prop (of type ${prop::class.simpleName})", e)
                }
            }

            toFill.rawStringFactory.properties.forEach {
                if (it is MutableProperty<*>) {
                    if (isDynamicRawStringFactory) {
                        if (rawStringMap.containsKey(it.id)) {
                            defaultParseBehavior(it)
                        } else {
                            (toFill.rawStringFactory as DynamicRawStringFactory).dynamicProperties.add(it)
                        }
                    } else if (rawStringMap.containsKey(it.id)) {
                        defaultParseBehavior(it)
                    }
                }
            }

            return toFill
        } catch (e: Throwable) {
            throw InvalidRawStringException("Caught an error while trying to parse (GenericGdObject) $toFill (of type ${toFill::class.simpleName}). Separator is '$separator' with the raw string: \"$rawString\"", e)
        }
    }
}