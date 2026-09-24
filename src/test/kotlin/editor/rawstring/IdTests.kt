package editor.rawstring

import TestTags
import editor.rawstring.Id.Companion.id
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag(TestTags.EDITOR)
private class IdTests {
    @Test
    @DisplayName("String ID test")
    fun stringCtorTest() {
        val id = "hi".id
        Assertions.assertEquals(Id.Type.STRING, id.type)
        Assertions.assertNull(id.numericalID)
        Assertions.assertEquals("hi", id.getID())
        Assertions.assertEquals("hi", id.getStringIdStrict())
        Assertions.assertThrows(NullPointerException::class.java) { id.getNumericalIdStrict() }

        Assertions.assertEquals(id, "hi".id)
    }

    @Test
    @DisplayName("Numerical ID test")
    fun numericalCtorTest() {
        val id = 5u.id
        Assertions.assertEquals(Id.Type.NUMERICAL, id.type)
        Assertions.assertNull(id.stringID)
        Assertions.assertEquals("5", id.getID())
        Assertions.assertEquals(5u, id.getNumericalIdStrict())
        Assertions.assertThrows(NullPointerException::class.java) { id.getStringIdStrict() }

        Assertions.assertEquals(id, 5.id)
        Assertions.assertEquals(id, 5u.id)

        Assertions.assertEquals((-5).id, 1.id)
    }

    @Test
    @DisplayName("Unknown ID constructor test")
    fun unknownCtorTest() {
        Assertions.assertEquals(Id.Type.STRING, Id.ofUnknown("hi").type)
        Assertions.assertEquals(Id.Type.NUMERICAL, Id.ofUnknown("5").type)
        Assertions.assertThrows(IllegalArgumentException::class.java) { Id.ofUnknown("-5") /* Invalid UInt */ }
    }
}