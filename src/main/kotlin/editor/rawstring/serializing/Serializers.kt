package editor.rawstring.serializing

import utils.toBooleanFromIntStrict
import kotlin.io.encoding.Base64

/**
 * Contains a list of **primitive** serializers
 */
object Serializers {
    val STRING: Serializer<String> = Serializer.create(
        { it },
        { it }
    )

    val B64STRING: Serializer<String> = Serializer.create(
        { Base64.UrlSafe.encode(it.toByteArray()) },
        { String(Base64.UrlSafe.decode(it.toByteArray())) }
    )

    val BOOLEAN: Serializer<Boolean> = Serializer.create(
        { if (it) "1" else "0" },
        String::toBooleanFromIntStrict
    )

    val INT: Serializer<Int> = Serializer.create(
        Int::toString,
        String::toInt
    )

    val UINT: Serializer<UInt> = Serializer.create(
        UInt::toString,
        String::toUInt
    )

    val UBYTE: Serializer<UByte> = Serializer.create(
        UByte::toString,
        String::toUByte
    )

    val FLOAT: Serializer<Float> = Serializer.create(
        Float::toString,
        String::toFloat
    )
}