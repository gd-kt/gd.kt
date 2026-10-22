package utils

import kotlin.reflect.KProperty

typealias CacheValueGetter<T> = () -> T

/**
 * Little utility function used to do caching:
 *
 * ```kotlin
 * val myIntValue: Int by remember {
 *     return /* ... */
 * }
 *
 * println(myIntValue) // By getting myIntValue, the lambda gets executed
 * ```
 */
class Cacher<T>(val valueGetter: CacheValueGetter<T>) {
    /**
     * The internal cached value.
     * If set to `null` then it was most likely never fetched
     */
    var cachedValue: T? = null

    var value: T
        get() {
            if (this.cachedValue == null)
                this.cachedValue = this.valueGetter()

            // We do this because if T is nullable (aka Type?)
            // then this.cachedValue will return Type? (because T = Type?)
            //
            // But if T is non-null
            // then it will correctly return a non-null value of Type
            @Suppress("UNCHECKED_CAST")
            return this.cachedValue as T
        }
        set(value) {
            this.cachedValue = value
        }

    fun invalidate() {
        this.cachedValue = null
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
        this.value

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }

    override fun toString(): String {
        return "${this::class.simpleName}(value = ${this.value})"
    }
}

/**
 * Caches a value given its getter
 * @param valueGetter the getter for the value
 * @return the [Cacher] storing the value when called
 */
fun <T> cache(valueGetter: CacheValueGetter<T>): Cacher<T> =
     Cacher(valueGetter)
