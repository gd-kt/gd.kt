package utils

import TestTags
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.IntProperty
import editor.rawstring.property.PropertyDefinition
import exceptions.IllegalTypeException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import kotlin.random.Random

private class UtilsTest {
    @Test
    fun isPrimitiveTest() {
        Assertions.assertTrue(isPrimitive("0"))
        Assertions.assertTrue(isPrimitive(0))
        Assertions.assertTrue(isPrimitive(0.0f))
        Assertions.assertTrue(isPrimitive(0.0))
        Assertions.assertTrue(isPrimitive(0u))
        Assertions.assertTrue(isPrimitive(0L))
        Assertions.assertTrue(isPrimitive(0.toByte()))
        Assertions.assertTrue(isPrimitive(0.toShort()))
        Assertions.assertTrue(isPrimitive(false))

        // Random classes
        Assertions.assertFalse(isPrimitive(arrayOf<Any>()))
        Assertions.assertFalse(isPrimitive(UtilsTest()))
        Assertions.assertFalse(isPrimitive(Random))
    }

    @Test
    @Tag(TestTags.CLIENT)
    @DisplayName("Map.toFormRequestBody Test")
    fun toFormRequestBodyTest() {
        val mapWithPrimitives = mapOf(
            Pair("hi", 0),
            Pair("hi 2", 1)
        )

        Assertions.assertDoesNotThrow { mapWithPrimitives.toFormRequestBody() }

        val mapWithSomePrimitives = mapOf(
            Pair(listOf<String>(), 0),
            Pair("hi 2", 1)
        )

        Assertions.assertThrows(IllegalTypeException::class.java) { mapWithSomePrimitives.toFormRequestBody() }

        val mapWithNoPrimitives = mapOf(
            Pair(listOf<String>(), byteArrayOf()),
            Pair(listOf(), byteArrayOf())
        )

        Assertions.assertThrows(IllegalTypeException::class.java) { mapWithNoPrimitives.toFormRequestBody() }
    }

    @Test
    @DisplayName("String.staticXor test")
    fun staticXorTest() {
        val string = "Hi hello !!! This is a cool string which is very long......".repeat(3)
        val key = Random.nextInt()

        val encryptedString = string.staticXor(key)

        Assertions.assertNotEquals(string, encryptedString)
        Assertions.assertEquals(string, encryptedString.staticXor(key))
        Assertions.assertNotEquals(string, encryptedString.cyclicXor(key))
    }

    @Test
    @DisplayName("String.cyclicXor(Int) test")
    fun cyclicXorIntKeyTest() {
        val string = "Hi hello !!! This is a cool string which is very long...... (v2)".repeat(3)
        val key = Random.nextInt()

        val encryptedString = string.cyclicXor(key)

        Assertions.assertNotEquals(string, encryptedString)
        Assertions.assertEquals(string, encryptedString.cyclicXor(key))
        Assertions.assertNotEquals(string, encryptedString.staticXor(key))
    }

    @Test
    @DisplayName("String.cyclicXor(String) test")
    fun cyclicXorStringKeyTest() {
        val string = "Hi hello !!! This is a cool string which is very long...... (v2)".repeat(3)

        val baseKey = "Wow look at this amazing key I just made. I hope no one will steal it (please don't, I have a family to feed !)"
        val key = baseKey.drop(Random.nextInt(baseKey.length - 5))

        val encryptedString = string.cyclicXor(key)

        Assertions.assertNotEquals(string, encryptedString)
        Assertions.assertEquals(string, encryptedString.cyclicXor(key))
    }

    @Test
    @Tag(TestTags.EDITOR)
    @DisplayName("Property collections test")
    fun propertyCollectionTest() {
        val myPropCollection = mutableListOf<PropertyDefinition<*>>()

        Assertions.assertFalse(myPropCollection.containsProperty(5.id))
        Assertions.assertThrows(NoSuchElementException::class.java) { myPropCollection.getProperty(5.id) }

        val prop = IntProperty(2.id)
        myPropCollection.add(prop)

        Assertions.assertFalse(myPropCollection.containsProperty(5.id))
        Assertions.assertThrows(NoSuchElementException::class.java) { myPropCollection.getProperty(5.id) }
        Assertions.assertTrue(myPropCollection.containsProperty(2.id))
        Assertions.assertDoesNotThrow { myPropCollection.getProperty(2.id) }

        prop.value = 7
        Assertions.assertEquals(7, myPropCollection.getProperty(2.id).value)
        Assertions.assertEquals(prop, myPropCollection.getProperty(2.id))

        myPropCollection.remove(prop)

        Assertions.assertFalse(myPropCollection.containsProperty(5.id))
        Assertions.assertThrows(NoSuchElementException::class.java) { myPropCollection.getProperty(5.id) }
    }

    @Test
    @DisplayName("Cacher test")
    fun cacherTest() {
        var calledLambdaAmount = 0
        val cacher = remember {
            calledLambdaAmount += 1
            12
        }
        var cachedValue by cacher

        Assertions.assertNull(cacher.cachedValue)
        Assertions.assertEquals(0, calledLambdaAmount)
        Assertions.assertEquals(12, cachedValue /* Calls Cacher.getValue + lambda */)
        Assertions.assertEquals(1, calledLambdaAmount)

        cachedValue = 20
        Assertions.assertEquals(20, cachedValue /* Calls Cacher.getValue */)
        Assertions.assertEquals(1, calledLambdaAmount)

        cacher.invalidate()
        Assertions.assertNull(cacher.cachedValue)
        Assertions.assertEquals(1, calledLambdaAmount)
        Assertions.assertEquals(12, cachedValue /* Calls Cacher.getValue + lambda */)
        Assertions.assertEquals(2, calledLambdaAmount)
    }
}