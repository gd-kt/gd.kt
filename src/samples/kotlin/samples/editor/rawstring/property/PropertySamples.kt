package samples.editor.rawstring.property

import editor.objects.GenericGdObject
import editor.rawstring.RawStringFactory
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.ConditionalProperty
import editor.rawstring.property.IntProperty
import editor.rawstring.property.MutableConditionalProperty
import editor.rawstring.serializing.Serializers

private fun isSerializableSample() {
    val prop = IntProperty(1.id, defaultValue = 5, currentValue = 5)

    // This returns false
    // The value is equal to the default value, so there's no need
    // to serialize it
    prop.isSerializable()

    prop.value = 7

    // This returns true
    // The value is NOT equal to the default value, so we need
    // to serialize it
    prop.isSerializable()
}

private fun conditionalPropertySample() {
    // Let's say you have properties that depend on each other.
    // In that case we have conditional properties !
    //
    // As its name suggest, it only outputs a raw string only IF the
    // predicate returns 'true'
    //
    // Here is an example:

    class MyObj : GenericGdObject {
        override val rawStringFactory: RawStringFactory = RawStringFactory.createDynamic(this)

        val normalProp = IntProperty(1.id, defaultValue = 0)
        val conditionalProp = ConditionalProperty(
            id = 2.id,
            dependantOn = this.normalProp,
            serializer = Serializers.BOOLEAN,
            predicate = { it.isSerializable() }
        ) {
            true // We always return 'true' as our value (aka "1" in geometry dash)
        }        // This also mean this is a "boolean" (conditional) property
    }

    // Then, we can take a look at the raw string:
    val obj = MyObj()
    obj.asRawString() // returns "" since we serialize nothing

    obj.normalProp.value = 5
    // But now, since the normalProp is serializable
    // the conditional property's predicate will return 'true':
    obj.asRawString() // returns "1,5,2,1"
}

private fun mutableConditionalPropertySample() {
    // Let's say you have properties that depend on each other.
    // In that case we have conditional properties !
    //
    // As its name suggest, it only outputs a raw string only IF the
    // predicate returns 'true'
    //
    // Here is an example:

    class MyObj : GenericGdObject {
        override val rawStringFactory: RawStringFactory = RawStringFactory.createDynamic(this)

        val normalProp = IntProperty(1.id, defaultValue = 0)
        val conditionalProp = MutableConditionalProperty(
            id = 2.id,
            defaultValue = true,
            currentValue = false,
            dependantOn = this.normalProp,
            serializer = Serializers.BOOLEAN,
            predicate = { it.isSerializable() }
        )
    }

    // Then, we can take a look at the raw string:
    val obj = MyObj()
    obj.asRawString() // returns "" since we serialize nothing
                      // (the conditional prop only gets enabled whenever the
                      // 'normalProp' is serializable, indicated by our predicate)

    obj.normalProp.value = 5
    // But now, since the normalProp is serializable
    // the conditional property's predicate will return 'true':
    obj.asRawString() // returns "1,5,2,1"

    // Now, this does the same thing as the immutable conditional property ('ConditionalProperty').
    // But, here is how this comes into play

    obj.conditionalProp.resetValue()
    // Now that the conditionalProp's value is back to its default value
    // it's no longer serializable.
    // The object's raw string now looks like this:
    obj.asRawString() // returns "1,5"
}
