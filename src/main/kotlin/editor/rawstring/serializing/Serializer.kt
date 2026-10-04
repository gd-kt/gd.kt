package editor.rawstring.serializing

import client.GDClientApi
import client.clients.AbstractGDClient
import client.struct.ServerStructure
import client.struct.ServerStructureCompanion
import editor.rawstring.RawStringable
import editor.rawstring.property.AbstractCollectionProperty
import editor.rawstring.property.CollectionCtor
import editor.rawstring.property.GdEnum
import kotlin.enums.EnumEntries

fun interface Serializable<in T> {
    companion object {
        /**
         * @see toString
         */
        fun <T> usingToString(): Serializable<T> = { value -> value.toString() }
    }

    fun serialize(value: T): String
}

fun interface Parsable<out T> {
    companion object {
        @JvmStatic
        @GDClientApi
        fun <S : ServerStructure, T : ServerStructureCompanion<S>> fromServerStruct(serverStruct: T, client: AbstractGDClient): Parsable<S> = {
            serverStruct.parse(it, client)
        }

        @JvmName("serverStructParsableToList")
        @JvmStatic
        @GDClientApi
        fun <S : ServerStructure, T : ServerStructureCompanion<S>> Parsable<S>.listParsable(separator: Char = AbstractCollectionProperty.ELEMENT_SEPARATOR): Parsable<List<S>> = {
            val res = arrayListOf<S>()
            val splitted = it.split(separator)
            splitted.forEach { value ->
                res.add(this.parse(value))
            }

            res
        }
    }

    fun parse(rawValue: String): T
}

interface Serializer<T> : Serializable<T>, Parsable<T> {
    companion object {
        @JvmStatic
        fun <T> create(serializer: (T) -> String, parser: (String) -> T): Serializer<T> {
            return object : Serializer<T> {
                override fun serialize(value: T): String = serializer(value)

                override fun parse(rawValue: String): T = parser(rawValue)
            }
        }

        @JvmStatic
        fun <T : RawStringable> fromRawstringable(parser: (String) -> T): Serializer<T> {
            return object : Serializer<T> {
                override fun serialize(value: T): String = value.asRawString()

                override fun parse(rawValue: String): T = parser(rawValue)
            }
        }

        @JvmStatic
        fun <T, C> collectionSerializer(
            collectionCtor: CollectionCtor<C>,
            elemSerializer: Serializer<T>,
            elemSeparator: Char = AbstractCollectionProperty.ELEMENT_SEPARATOR
        ): Serializer<C> where C : MutableCollection<T> =
            create(
                { it.joinToString(elemSeparator.toString(), transform = elemSerializer::serialize) },
                {
                    val coll = collectionCtor()
                    val parsedElems = it.split(elemSeparator).map(elemSerializer::parse)
                    coll.addAll(parsedElems)

                    return@create coll
                }
            )

        fun <T, C> Serializer<T>.collection(collectionCtor: CollectionCtor<C>, elemSeparator: Char = AbstractCollectionProperty.ELEMENT_SEPARATOR): Serializer<C> where C : MutableCollection<T> =
            collectionSerializer(collectionCtor, this, elemSeparator)


        @JvmStatic
        fun clampedInt(range: IntRange): Serializer<Int> =
            create(
                { it.coerceIn(range).toString() },
                { it.toInt().coerceIn(range) }
            )

        @JvmStatic
        fun clampedUInt(range: UIntRange): Serializer<UInt> =
            create(
                { it.coerceIn(range).toString() },
                { it.toUInt().coerceIn(range) }
            )

        @JvmStatic
        fun clampedFloat(range: ClosedFloatingPointRange<Float>): Serializer<Float> =
            create(
                { it.coerceIn(range).toString() },
                { it.toFloat().coerceIn(range) }
            )

        @JvmStatic
        @Suppress("MoveLambdaOutsideParentheses")
        fun <T> enum(enumEntries: EnumEntries<T>): Serializer<T> where T : Enum<T>, T : GdEnum =
            create(
                { it.value.toString() },
                { str -> enumEntries.first { it.value == str.toInt() } }
            )
    }
}

/**
 * Creates a new [Serializable] that is hooked
 * @param hooker the lambda used to transform the value
 * @return the hooked [Serializable]
 */
fun <T> Serializable<T>.hook(hooker: (value: T) -> T): Serializable<T> = {
    this.serialize(hooker(it))
}

/**
 * Creates a new [Parsable] that is hooked
 * @param hooker the lambda used to transform the raw value
 * @return the hooked [Parsable]
 */
fun <T> Parsable<T>.hook(hooker: (rawValue: String) -> String): Parsable<T> = {
    this.parse(hooker(it))
}

/**
 * Creates a new [Serializer] where the [serializer][Serializable] is hooked
 * @param hooker the lambda used to transform the value
 * @return the hooked [Parsable]
 */
fun <T> Serializer<T>.hookSerializer(hooker: (value: T) -> T): Serializer<T> = Serializer.create(
    { this.serialize(hooker(it)) },
    ::parse
)

/**
 * Creates a new [Serializer] where the [parser][Parsable] is hooked
 * @param hooker the lambda used to transform the raw value
 * @return the hooked [Parsable]
 */
@Suppress("MoveLambdaOutsideParentheses")
fun <T> Serializer<T>.hookParser(hooker: (rawValue: String) -> String): Serializer<T> = Serializer.create(
    ::serialize,
    { this.parse(hooker(it)) }
)
