package editor.rawstring

import editor.objects.GenericGdObject
import editor.rawstring.property.AbstractProperty
import editor.rawstring.property.PropertyDefinition

internal class DynamicRawStringFactoryImpl(parent: GenericGdObject, keyValSeparator: Char = AbstractProperty.KEY_VAL_SEPARATOR) :
    RawStringFactoryImpl(parent, keyValSeparator), DynamicRawStringFactory
{
    override val dynamicProperties: MutableList<PropertyDefinition<*>> = arrayListOf()
}