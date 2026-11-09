package editor.objects

import TestTags
import editor.objects.data.Pos
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.IntProperty
import editor.rawstring.property.ListProperty
import editor.rawstring.serializing.Serializers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

class NotGenericObject {
    val intProperty = IntProperty(1.id, defaultValue = 5)
    val listProperty = ListProperty(2.id, elemSerializer = Serializers.INT, defaultValue = mutableListOf(5, 4, 3, 2))

    fun asRawString(): String =
        "${intProperty.asRawString()},${listProperty.asRawString()}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NotGenericObject) return false

        if (this.intProperty.value != other.intProperty.value) return false
        if (this.listProperty.value != other.listProperty.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = this.intProperty.hashCode()
        result = 31 * result + this.listProperty.hashCode()
        return result
    }
}

@Tag(TestTags.EDITOR)
private class ObjectParserTests {
    @Test
    @DisplayName("ObjectParser.parse (GenericGDObject) Test -> DynamicPropertiesBehavior")
    fun dynamicPropertiesBehaviorTest() {
        Assertions.assertDoesNotThrow {
            val obj1 = ObjectParser.parseGdObject(
                "1,1,2,0,3,0,1789,100",
                SimpleObject(1u, Pos.ZERO),
                dynamicPropertiesBehavior = ObjectParser.DynamicPropertiesBehavior.ADD
            )

            Assertions.assertEquals(1, obj1.rawStringFactory.dynamicProperties.size)
            Assertions.assertEquals("100", obj1.rawStringFactory.dynamicProperties.first().value)
        }

        Assertions.assertDoesNotThrow {
            val obj2 = ObjectParser.parseGdObject("1,1,2,0,3,0,1789,100", SimpleObject(1u, Pos.ZERO), dynamicPropertiesBehavior = ObjectParser.DynamicPropertiesBehavior.IGNORE)
            Assertions.assertEquals(0, obj2.rawStringFactory.dynamicProperties.size)
        }

        Assertions.assertThrows(ObjectParser.HasDynamicPropertiesException::class.java) {
            ObjectParser.parseGdObject("1,1,2,0,3,0,1789,100", SimpleObject(1u, Pos.ZERO), dynamicPropertiesBehavior = ObjectParser.DynamicPropertiesBehavior.THROW_EXCEPTION)
        }
    }

    @Test
    @DisplayName("ObjectParser.parse (GenericGDObject) Test")
    fun objectParserTestGeneric() {
        val obj = SimpleObject(2u, 0f, 0f)
        var parsedObj = ObjectParser.parse(obj.asRawString(), SimpleObject(0u, 0f, 0f))
        Assertions.assertEquals(obj, parsedObj)

        obj.linkedGroupID.value = 5
        parsedObj = ObjectParser.parse(SimpleObject(2u, 0f, 0f).asRawString(), SimpleObject(0u, 0f, 0f))
        Assertions.assertNotEquals(obj, parsedObj)
    }

    @Test
    @DisplayName("ObjectParser.parseGdObject (GenericGDObject) Test")
    fun objectParserTestGenericObject() {
        val obj = SimpleObject(2u, 0f, 0f)
        var parsedObj = ObjectParser.parseGdObject(obj.asRawString(), SimpleObject(0u, 0f, 0f))
        Assertions.assertEquals(obj, parsedObj)

        obj.linkedGroupID.value = 5
        parsedObj = ObjectParser.parseGdObject(SimpleObject(2u, 0f, 0f).asRawString(), SimpleObject(0u, 0f, 0f))
        Assertions.assertNotEquals(obj, parsedObj)


        val objRawStringWithAdditionalProperties = obj.asRawString() + ",1789,5"
        parsedObj = ObjectParser.parseGdObject(objRawStringWithAdditionalProperties, SimpleObject(0u, 0f, 0f))
        Assertions.assertNotEquals(obj, parsedObj)
        Assertions.assertEquals(1, parsedObj.rawStringFactory.dynamicProperties.size)

        Assertions.assertEquals("5", parsedObj.rawStringFactory.dynamicProperties.first().value)
    }

    @Test
    @DisplayName("ObjectParser.parseAny (GenericGDObject) Test")
    fun objectParserTestGenericAny() {
        val obj = SimpleObject(2u, 0f, 0f)
        var parsedObj = ObjectParser.parseAny(obj.asRawString(), SimpleObject(0u, 0f, 0f))
        Assertions.assertEquals(obj, parsedObj)

        obj.linkedGroupID.value = 5
        parsedObj = ObjectParser.parseAny(SimpleObject(2u, 0f, 0f).asRawString(), SimpleObject(0u, 0f, 0f))
        Assertions.assertNotEquals(obj, parsedObj)
    }

    @Test
    @DisplayName("ObjectParser.parse (Any) Test")
    fun objectParserTestAny() {
        val obj = NotGenericObject()
        var parsedObj = ObjectParser.parse(obj.asRawString(), NotGenericObject())
        Assertions.assertEquals(obj, parsedObj)

        obj.intProperty.value = 3
        parsedObj = ObjectParser.parse(NotGenericObject().asRawString(), NotGenericObject())
        Assertions.assertNotEquals(obj, parsedObj)
    }

    @Test
    @DisplayName("ObjectParser.parseAny (Any) Test")
    fun objectParserTestParseAny() {
        val obj = NotGenericObject()
        var parsedObj = ObjectParser.parseAny(obj.asRawString(), NotGenericObject())
        Assertions.assertEquals(obj, parsedObj)

        obj.intProperty.value = 3
        parsedObj = ObjectParser.parseAny(NotGenericObject().asRawString(), NotGenericObject())
        Assertions.assertNotEquals(obj, parsedObj)
    }
}