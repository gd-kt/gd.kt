@file:JvmName("GdDotKTUtils")
package utils

import editor.rawstring.Id
import editor.rawstring.property.PropertyDefinition
import exceptions.IllegalTypeException
import okhttp3.FormBody

fun isPrimitive(value: Any) =
    // We allow UINTs because they are ints
    value is CharSequence || value is UInt || value::class.javaPrimitiveType != null

/**
 * Returns this boolean as an int.
 * It is `0` when `false` and `1` when `true`
 */
fun Boolean.toInt(): Int =
    if (this)
        1
    else
        0

/**
 * Turns this string into a boolean by converting it to an int.
 * This can be simplified to: `str -> str.toInt() -> num == 1`.
 *
 * This also checks if the string is equal to `true` or `false` beforehand
 * @return the string's boolean representation
 */
fun String.toBooleanFromInt(): Boolean {
    if (this.lowercase().toBooleanStrictOrNull() == null)
        return this.trim() == "1"

    return this.toBooleanStrict()
}

/**
 * Turns this string into a boolean by converting it to an int.
 * This can be simplified to: `str -> str.toInt() -> num == 1`.
 *
 * This also checks if the string is equal to `true` or `false` beforehand
 * @return the string's boolean representation
 * @throws IllegalArgumentException if the string is not equal to `0` or `1`
 */
fun String.toBooleanFromIntStrict(): Boolean {
    if (this.toBooleanStrictOrNull() == null)
        return when (this.lowercase().trim()) {
            "0" -> false
            "1" -> true
            else -> throw IllegalArgumentException("The string doesn't represent a boolean value: $this")
        }

    return this.toBooleanStrict()
}

/**
 * Turns this string into a boolean by converting it to an int.
 * This can be simplified to: `str -> str.toInt() -> num == 1`.
 *
 * This also checks if the string is equal to `true` or `false` beforehand
 * @return the string's boolean representation, or `null` if the string is not equal to `0` or `1`
 */
fun String.toBooleanFromIntStrictOrNull(): Boolean? {
    if (this.toBooleanStrictOrNull() == null)
        return when (this.lowercase().trim()) {
            "0" -> false
            "1" -> true
            else -> null
        }

    return this.toBooleanStrictOrNull()
}

// The 'Any' upper bound is to prevent null types
fun <K : Any, V : Any> Map<K, V>.toFormRequestBody(): FormBody.Builder {
    val bodyBuilder = FormBody.Builder()
    this.forEach { (k, v) ->
        if (!isPrimitive(k))
            throw IllegalTypeException("Key '$k''s type (${k::class.simpleName}) is not a primitive and so cannot get turned into a form key")

        if (!isPrimitive(v))
            throw IllegalTypeException("Value '$v''s type (${v::class.simpleName}) is not a primitive and so cannot get turned into a form key")

        bodyBuilder.add(k.toString(), v.toString())
    }

    return bodyBuilder
}

/**
 * Performs a "static xor" operation on this string with the given [key].
 * This is taken from [boomlings.dev](https://boomlings.dev/topics/encryption/xor#singular)
 * @param key the key to encrypt the content with
 * @return the encrypted value.
 *         This value can be brought back to its original string by
 *         executing this function on the encrypted string with
 *         the same key:
 *
 *         val myString = "Hi hello"
 *         val myKey = 1291
 *
 *         val myEncryptedString = myString.staticXor(myKey)
 *         assertEquals(myString, myEncryptedString.staticXor(myKey))
 *
 * @see cyclicXor
 */
fun String.staticXor(key: Int): String {
    var res = ""
    this.forEach {
        res += (it.code xor key).toChar()
    }

    return res
}

/**
 * Performs a "cyclic xor" operation on this string with the given [key]
 * This is taken from [boomlings.dev](https://boomlings.dev/topics/encryption/xor#cycle)
 * @param key the key to encrypt the content with.
 *            The key encode individual parts of the string, unlike
 *            the [staticXor] where the key is used to encode
 *            the entire string
 * @return the encrypted value.
 *         This value can be brought back to its original string by
 *         executing this function on the encrypted string with
 *         the same key:
 *
 *         val myString = "Hi hello"
 *         val myKey = "1291"
 *
 *         val myEncryptedString = myString.cyclicXor(myKey)
 *         assertEquals(myString, myEncryptedString.cyclicXor(myKey))
 *
 * @see staticXor
 */
fun String.cyclicXor(key: String): String {
    val byteString = this.toByteArray()
    val result = CharArray(byteString.size)

    byteString.forEachIndexed { i, b ->
        result[i] = (byteString[i].toInt() xor key[i % key.length].code).toChar()
    }

    return result.concatToString()
}

/**
 * Performs a "cyclic xor" operation on this string with the given [key]
 * This is taken from [boomlings.dev](https://boomlings.dev/topics/encryption/xor#cycle)
 * @param key the key to encrypt the content with.
 *            The key encode individual parts of the string, unlike
 *            the [staticXor] where the key is used to encode
 *            the entire string
 * @return the encrypted value.
 *         This value can be brought back to its original string by
 *         executing this function on the encrypted string with
 *         the same key:
 *
 *         val myString = "Hi hello"
 *         val myKey = "1291"
 *
 *         val myEncryptedString = myString.cyclicXor(myKey)
 *         assertEquals(myString, myEncryptedString.cyclicXor(myKey))
 *
 * @see staticXor
 */
fun String.cyclicXor(key: Int) = this.cyclicXor(key.toString())

/**
 * Quotes this string
 * @return `"$this"`
 */
fun CharSequence?.quote(): String =
    "\"$this\""

internal inline fun <T : Any> nonNull(valueGetter: () -> T?): T =
    valueGetter()!!

internal inline fun <T : Any> nonNull(exception: RuntimeException, valueGetter: () -> T?): T =
    valueGetter() ?: throw exception

internal inline fun <T : Any> nonNull(message: String, valueGetter: () -> T?): T =
    nonNull(NullPointerException(message), valueGetter)

/**
 * This is not like kotlin's T0DO() function and is instead used for tests to prevent
 * show useless TODOs from showing up
 */
@Suppress("FunctionName")
internal fun LACKS_IMPL(): Nothing = throw NotImplementedError("This doesn't have any implementation. This is maybe because we are in a test environment and this has no reason to be implemented.")

/**
 * Gets the first property with the given [id]
 * @param id the [Id] to look for
 * @return the found [property][PropertyDefinition]
 * @throws NoSuchElementException if there is no property corresponding to the given [id]
 */
fun <T : PropertyDefinition<*>> Collection<T>.getProperty(id: Id): PropertyDefinition<*> =
    this.first { it.id == id }

/**
 * Checks if this collection contains a property with the given [id]
 * @param id the [Id] to look for
 * @return if the given [id] is contained in this collection
 */
fun <T : PropertyDefinition<*>> Collection<T>.containsProperty(id: Id): Boolean =
    this.firstOrNull { it.id == id } != null
