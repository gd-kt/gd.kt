package editor.rawstring

import editor.objects.GenericGdObject
import editor.rawstring.RawStringFactory.Companion.createRawString
import editor.rawstring.property.AbstractProperty
import editor.rawstring.property.PropertyDefinition

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