package editor.rawstring

import editor.objects.GenericGdObject
import editor.rawstring.RawStringFactory.Companion.createRawString
import editor.rawstring.property.AbstractProperty
import editor.rawstring.property.PropertyDefinition
import utils.containsProperty
import utils.getProperty

/**
 * A raw string factory allows you to abstract the generation of raw string for [GenericGdObjects][GenericGdObject].
 * Internally, the **default implementation** uses reflection to look for [PropertyDefinitions][PropertyDefinition] and create the raw string from there.
 *
 * A <b>dynamic</b> raw string factory however allows the user to add any properties
 * dependant of the factory's linked object at **runtime**.
 * @see RawStringFactory
 */
interface DynamicRawStringFactory : RawStringFactory {
    /**
     * A mutable list of custom properties provided by this dynamic raw string factory.
     */
    val dynamicProperties: MutableList<PropertyDefinition<*>>

    fun getDynamicSerializableProperties(): List<PropertyDefinition<*>> =
        this.dynamicProperties
            .stream()
            .filter { it.isSerializable() }
            .toList()

    override fun asRawString(): String {
        val dynamicSerializableProperties = this.getDynamicSerializableProperties()
        return if (dynamicSerializableProperties.isEmpty()) {
            super.asRawString()
        } else {
            val serializedCustomProperties = createRawString(dynamicSerializableProperties, this.keyValSeparator)
            super.asRawString() + AbstractProperty.KEY_VAL_SEPARATOR + serializedCustomProperties
        }
    }
}

/**
 * Gets the first dynamic property with the given [id]
 * @param id the [Id] to look for
 * @return the found [property][PropertyDefinition]
 * @throws NoSuchElementException if there is no property corresponding to the given [id]
 * @see dynamicProperties
 */
fun DynamicRawStringFactory.getDynamicProperty(id: Id): PropertyDefinition<*> =
    this.dynamicProperties.getProperty(id)

/**
 * Checks if this dynamic raw string factory contains a property with the given [id]
 * @param id the [Id] to look for
 * @return if the given [id] is contained in this dynamic raw string factory
 * @see dynamicProperties
 */
fun DynamicRawStringFactory.containsDynamicProperty(id: Id): Boolean =
    this.dynamicProperties.containsProperty(id)
