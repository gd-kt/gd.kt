package editor.rawstring

import editor.objects.GenericGdObject
import editor.rawstring.property.AbstractProperty
import editor.rawstring.property.PropertyDefinition
import java.util.*
import kotlin.reflect.KVisibility
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

internal open class RawStringFactoryImpl(private val parent: GenericGdObject, override val keyValSeparator: Char = AbstractProperty.KEY_VAL_SEPARATOR) : RawStringFactory {
    private var cachedProperties: Collection<PropertyDefinition<*>>? = null

    override val properties: Collection<PropertyDefinition<*>>
        get() {
            if (this.cachedProperties == null)
                this.computeProperties { props ->
                    this.cachedProperties = Collections.unmodifiableCollection(props)
                }

            return this.cachedProperties!!
        }

    private fun computeProperties(consumer: (List<PropertyDefinition<*>>) -> Unit) {
        val props = mutableListOf<PropertyDefinition<*>>()

        this.parent::class.memberProperties.forEach {
            if (it.visibility == KVisibility.PUBLIC && it.returnType.isSubtypeOf(PropertyDefinition::class.starProjectedType)) {
                val prop = it.getter.call(this.parent)
                if (prop != null)
                    props.add(prop as PropertyDefinition<*>)
            }
        }

        consumer(props.sortedBy { it.id })
    }

    override fun asMap(): Map<Id, PropertyDefinition<*>> {
        val res = mutableMapOf<Id, PropertyDefinition<*>>()
        this.getSerializableProperties().forEach {
            res[it.id] = it
        }

        return res
    }
}