# gd.kt contributing rules

## General rules

This project is split in 2 main parts/APIs:
- Editor (`fr.geming400.gddotkt.editor`)
- Client (`fr.geming400.gddotkt.client`)

The "editor" part handles everything raw string related and level related.<br>
The "client" part handles everything to simulate a Geometry Dash client.
This is where the requests are sent.

Samples should be provided if necessary. Only do this on complex functions,
most stuff don't need any samples !

---

Kotlin's coding guidelines should be followed.
There are some minor exceptions however.

### General guidelines

#### Annotations

Unlike in kotlin's coding guidelines, annotations shouldn't be placed
on the same line as other ones:

```kotlin
// Not allowed
@MyAnnot @JvmStatic
@Thing("hi")
fun myFunc(): Nothing = TODO()

// Allowed
@MyAnnot
@JvmStatic
@Thing("hi")
fun myFunc(): Nothing = TODO()
```

#### Companion objects

Companion objects must be placed at the start of a class, not the end:
```kotlin
class Foo {
    companion object {
        // ...
    }
    
    // ...
}
```

### Editor

If you end up modifying **objects** and are adding properties, you should
annotate the property with `@GDName` if necessary. This annotation's KDoc explains
when to use it:
> Represents the actual name of the property in geometry dash. Some properties don't have names (eg: obj id)
> but some have, like in the `extra` tab.
> 
> This is mostly useful when a property's name was renamed
> in the code to make more sense, while in geometry dash
> it might be more ambiguous.

### Client

If you are adding support for an endpoint,
you always must add support in the `GDClient` and `AsyncGDClient` classes.

If you are adding a custom structure to be parsed:
- You must always place it in the `fr.geming400.gddotkt.client.struct` package
- You must always format it like this:
```kotlin
class MyStructure(override val client: AbstractGDClient) : ServerStructure {
    companion object : ServerStructureCompanion<MyStructure> {
        override val separator: Char = ':'

        override fun parse(rawString: String, client: AbstractGDClient): MyStructure =
            ObjectParser.parse(rawString, MyStructure(client), separator)
    }
    
    // ...
}
```

## Tests

Tests, if possible, must always be added.
If tests are going to be added, they must be like this:

```kotlin
private class MyTest {
    @Test
    fun myTest() {
        // ...
    }
}
```

Tags must also **always** be added *(unless they do not relate to the editor/client api)*:
```kotlin
private class MyTest {
    @Test
    @Tag(TestTags.EDITOR)
    fun myTest() {
        // ...
    }
}

@Tag(TestTags.EDITOR)
private class MyClientTest {
    @Test
    fun myTest() {
        // ...
    }
}
```

`@Tag("Your tag")` is not allowed ! The tag names must always come from `TestTags`.
`@Tag` annotation must also be placed at the end of the annotation chain if possible

---

If needed, "custom assertions" can be added in the `CustomAssertions` class.
