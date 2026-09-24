@file:Suppress("UnusedVariable")

package samples.editor.objects

import editor.objects.GenericGdObject
import editor.objects.ObjectParser
import editor.rawstring.RawStringFactory
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.IntProperty

private fun parseAnySample() {
    // Object parsers can parse objects with no constructors
    // as well with one with constructors
    open class MyObj {
        val prop = IntProperty(5.id, defaultValue = 6)
    }

    // Since MyObj.prop has a default value it defaults to it
    val myObj = ObjectParser.parseAny<MyObj>("")
    // myObj2.prop.value is equal to 6

    val myObj2 = ObjectParser.parseAny<MyObj>("5,7")
    // myObj2.prop.value is equal to 7

    class MyObjCtor(private val number: Int) : MyObj()

    // Since MyObj.prop has a default value it defaults to it
    val myObjCtor = ObjectParser.parseAny("", MyObjCtor(1))
    // myObj2.prop.value is equal to 6
    // myObj2.number is equal to 1. Since it's not a property its value doesn't change

    val myObjCtor2 = ObjectParser.parseAny("5,7", MyObjCtor(1))
    // myObj2.prop.value is equal to 7
    // myObj2.number is equal to 1
}

private fun parseGenericGdObjectSample() {
    // Object parsers can parse objects with no constructors
    // as well with one with constructors
    open class MyObj : GenericGdObject {
        override val rawStringFactory: RawStringFactory = RawStringFactory.create(this)

        val prop = IntProperty(5.id, defaultValue = 6)
    }

    // Since MyObj.prop has a default value it defaults to it
    val myObj = ObjectParser.parseAny<MyObj>("")
    // myObj2.prop.value is equal to 6

    val myObj2 = ObjectParser.parseAny<MyObj>("5,7")
    // myObj2.prop.value is equal to 7

    class MyObjCtor(private val number: Int) : MyObj()

    // Since MyObj.prop has a default value it defaults to it
    val myObjCtor = ObjectParser.parseGdObject("", MyObjCtor(1))
    // myObj2.prop.value is equal to 6
    // myObj2.number is equal to 1. Since it's not a property its value doesn't change

    val myObjCtor2 = ObjectParser.parseGdObject("5,7", MyObjCtor(1))
    // myObj2.prop.value is equal to 7
    // myObj2.number is equal to 1
}
