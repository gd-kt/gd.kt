package editor.rawstring.serializing

import TestTags
import client.GDClientApi
import client.clients.GDClient
import client.struct.CommentUserInfo
import editor.objects.data.Hsv
import editor.rawstring.property.getOrThrow
import editor.rawstring.serializing.Parsable.Companion.listParsable
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import kotlin.io.encoding.Base64

@Tag(TestTags.EDITOR)
private class SerializerTests {
    fun <T> parsingAndSerializingTest(serializer: Serializer<T>, input: T, expectedSerializedValue: Any) {
        val serializedValue = serializer.serialize(input)
        Assertions.assertEquals(expectedSerializedValue.toString(), serializedValue)
        Assertions.assertEquals(input, serializer.parse(serializedValue))
    }

    @Test
    @DisplayName("Serializers.STRING test")
    fun stringSerializerTest() {
        parsingAndSerializingTest(Serializers.STRING, "hi", "hi")
    }

    @Test
    @DisplayName("Serializers.B64STRING test")
    fun b64stringSerializerTest() {
        parsingAndSerializingTest(Serializers.B64STRING, "hi", Base64.UrlSafe.encode("hi".toByteArray()))
    }

    @Test
    @DisplayName("Serializers.BOOLEAN test")
    fun booleanSerializerTest() {
        parsingAndSerializingTest(Serializers.BOOLEAN, false, 0)
        parsingAndSerializingTest(Serializers.BOOLEAN, true, 1)
    }

    @Test
    @DisplayName("Serializers.INT test")
    fun intSerializerTest() {
        parsingAndSerializingTest(Serializers.INT, -43, -43)
        parsingAndSerializingTest(Serializers.INT, 76, 76)
    }

    @Test
    @DisplayName("Serializers.UINT test")
    fun uintSerializerTest() {
        parsingAndSerializingTest(Serializers.UINT, 8u, 8)
    }

    @Test
    @DisplayName("Serializers.UBYTE test")
    fun ubyteSerializerTest() {
        parsingAndSerializingTest(Serializers.UBYTE, 8u, 8)
    }

    @Test
    @DisplayName("Serializers.USHORT test")
    fun ushortSerializerTest() {
        parsingAndSerializingTest(Serializers.USHORT, 8u.toUShort(), 8.toUShort())
    }

    @Test
    @DisplayName("Serializers.FLOAT test")
    fun floatSerializerTest() {
        parsingAndSerializingTest(Serializers.FLOAT, -7.1f, -7.1f)
        parsingAndSerializingTest(Serializers.FLOAT, 94f, 94f)
    }

    @Test
    @DisplayName("Serializers.HSV test")
    fun hsvSerializerTest() {
        val firstHsv = Hsv(40, 2f, 1f)
        parsingAndSerializingTest(Hsv.SERIALIZER, firstHsv, firstHsv.asRawString())
        parsingAndSerializingTest(Hsv.SERIALIZER, firstHsv, "40a2.0a1.0a0a0")

        val secondHsv = Hsv.checkedSatBrightness(40, 0.5f, 1f)
        parsingAndSerializingTest(Hsv.SERIALIZER, secondHsv, secondHsv.asRawString())
        parsingAndSerializingTest(Hsv.SERIALIZER, secondHsv, "40a0.5a1.0a1a1")
    }

    @Test
    @DisplayName("ServerStructure parser test")
    @OptIn(GDClientApi::class)
    fun serverStructParserTest() {
        val client = GDClient()
        val parser = Parsable.fromServerStruct(CommentUserInfo, client)
        val struct = CommentUserInfo(client).also {
            it.username.value = "Hi"
        }

        Assertions.assertEquals(struct.username, parser.parse("1~Hi").username)
    }

    @Test
    @DisplayName("ServerStructure parser test (list)")
    @OptIn(GDClientApi::class)
    fun serverStructListParserTest() {
        val client = GDClient()
        val parser = Parsable.fromServerStruct(CommentUserInfo, client).listParsable(separator = '|')

        val structs = listOf(
            CommentUserInfo(client).also { it.username.value = "Hi" },
            CommentUserInfo(client).also { it.username.value = "Bye" }
        )

        Assertions.assertEquals(structs.map { it.username.getOrThrow() }, parser.parse("1~Hi|1~Bye").map { it.username.getOrThrow() })
    }
}
