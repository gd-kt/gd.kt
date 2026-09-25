<div align="center">
    <img src="https://github.com/gd-kt/docs/raw/main/docs/public/alt_logo.png" height=35% width=35% alt="gd.kt logo">
</div>

# gd.kt

[![Jitpack Badge](https://jitpack.io/v/gd-kt/gd.kt.svg)](https://jitpack.io/#gd-kt/gd.kt)

*(This is actually the lib I've remade the most)*

This is the 3rd gd.lang lib I've made (it's predecessor is [gddotpy v2](https://github.com/Geming400/gddotpy-v2)).
This is mostly just to do a bit more kotlin and learn it more deeply.

It can be used, but do not really expect anything, though the difference with most geometry dash libraries is that this one allows you to create objects using an easy to use property system.

There is not much java interoperability because for example `UInt`s cannot get created on java and fully used.

> [!NOTE]
> `gd.kt`'s documentation can be found [here](https://gd-kt.github.io/docs/).

## Installation

You can install this lib via JitPack:
```kts
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.gd-kt:gd.kt:<version tag>")
}
```

## Usage

### Objects

`gd.kt` allows you to create objects.
You can look in the `fr.geming400.gddotkt.editor.objects` package or see the inheritors of `SimpleObject`.

You can also create your own object instances by extending the different open classes, like:
- SimpleObject
- ComplexObject
- TriggerObject

To add properties, you can look at the [gd info explorer](https://flowvix.github.io/gd-info-explorer/props) website
to know their ids, then you can add properties. For example:
```kt
class MyObjClass : ComplexObject {
    val myIntProp = IntProperty(1.id, defaultValue = 2)
    val myEnumProp = EnumProperty(1.id, Serializer.enum(MyEnum.entries))
}
```

And then you can get something called a "raw string" which is the raw
representation of the object via:
```kt
MyObjClass().asRawString()
```

### Parsing 

Raw strings can get parsed into any class.
Using the `ObjectParser` object you can input a raw string and get an output:
```kt
val obj = SimpleObject(5u, 12, 16)
// The "SimpleObject" provided is a dummy instance
// every properties from the raw string is going to get automatically filled
val obj2 = ObjectParser.parse(obj.asRawString(), SimpleObject(1u, 0, 0))
assertEquals(obj, obj2)
```

Object parsers can parse `GenericGdObject`s and any classes.

## AI

Almost no AI was used in this project. I make my projects by myself.

However, some large classes like structures for the client api are
really boring to make since it's really long, and it's not really
interesting to do.
