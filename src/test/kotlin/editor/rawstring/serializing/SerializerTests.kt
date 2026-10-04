package editor.rawstring.serializing

import TestTags
import editor.objects.data.Hsv
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
}
