package editor.objects

import CustomAssertions
import TestTags
import editor.objects.common.Speed
import editor.objects.common.SpeedPortalObject
import editor.objects.data.Pos
import editor.objects.data.Scale
import editor.objects.triggers.objects.*
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.UIntProperty
import editor.rawstring.property.getOrThrow
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import java.awt.Color
import kotlin.test.Test

@Tag(TestTags.EDITOR)
private class SimpleObjectTests {
    @Test
    @DisplayName("GenericGdObject.get operator test")
    fun getOperatorTest() {
        val obj = SimpleObject(0u, 0f, 0f)

        Assertions.assertThrows(NoSuchElementException::class.java) { obj[9999.id] }
        Assertions.assertDoesNotThrow { obj[1.id] }
        Assertions.assertEquals(UIntProperty::class, obj[1.id]::class)
    }

    @Test
    @DisplayName("SimpleObject.pos test")
    fun posTest() {
        val obj = SimpleObject(0u, 0f, 0f)
        Assertions.assertEquals(0f, obj.x.value)
        Assertions.assertEquals(0f, obj.y.value)
        Assertions.assertEquals(Pos(0f, 0f), obj.pos)

        obj.pos = Pos(5f, 2f)
        Assertions.assertEquals(5f, obj.x.value)
        Assertions.assertEquals(2f, obj.y.value)
        Assertions.assertEquals(Pos(5f, 2f), obj.pos)

        obj.setPos(6f, 3f)
        Assertions.assertEquals(6f, obj.x.value)
        Assertions.assertEquals(3f, obj.y.value)
        Assertions.assertEquals(Pos(6f, 3f), obj.pos)
    }

    @Test
    @DisplayName("SimpleObject.scale test")
    fun scaleTest() {
        val obj = SimpleObject(0u, 0f, 0f)
        Assertions.assertEquals(1f, obj.scaleX.value)
        Assertions.assertEquals(1f, obj.scaleY.value)
        Assertions.assertEquals(Scale(1f, 1f), obj.scale)

        obj.scale = Scale(5f, 2f)
        Assertions.assertEquals(5f, obj.scaleX.value)
        Assertions.assertEquals(2f, obj.scaleY.value)
        Assertions.assertEquals(Scale(5f, 2f), obj.scale)

        obj.setScale(6f, 3f)
        Assertions.assertEquals(6f, obj.scaleX.value)
        Assertions.assertEquals(3f, obj.scaleY.value)
        Assertions.assertEquals(Scale(6f, 3f), obj.scale)
    }

    @Test
    fun equalityTest() {
        val firstObj = SimpleObject(0u, 0f, 0f)
        val secondObj = SimpleObject(0u, 0f, 0f)

        Assertions.assertTrue(firstObj == secondObj)
        Assertions.assertFalse(firstObj === secondObj)

        firstObj.linkedGroupID.value = 5
        Assertions.assertFalse(firstObj == secondObj)

        secondObj.linkedGroupID.value = 5
        Assertions.assertTrue(firstObj == secondObj)
    }
}

@Tag(TestTags.EDITOR)
private class ComplexObjectTests {
    @Test
    fun moveTriggerTest() {
        val moveTrigger = MoveTrigger(Pos.ZERO)
        Assertions.assertEquals(Pos.ZERO, moveTrigger.moveVector)
        Assertions.assertEquals(LockOn.NONE, moveTrigger.lockOnX)
        Assertions.assertEquals(LockOn.NONE, moveTrigger.lockOnY)

        // Move vector

        moveTrigger.moveX.value = 5f
        moveTrigger.moveY.value = 15f
        Assertions.assertEquals(Pos(5f, 15f), moveTrigger.moveVector)

        moveTrigger.moveVector = Pos(8f, 7f)
        Assertions.assertEquals(Pos(8f, 7f), moveTrigger.moveVector)

        // Lock on

        moveTrigger.lockOnPlayerX.value = true
        Assertions.assertEquals(LockOn.PLAYER, moveTrigger.lockOnX)

        moveTrigger.lockOnPlayerX.value = false
        moveTrigger.lockOnCameraX.value = true
        Assertions.assertEquals(LockOn.CAMERA, moveTrigger.lockOnX)

        moveTrigger.lockOnCameraX.value = false
        Assertions.assertEquals(LockOn.NONE, moveTrigger.lockOnX)

        moveTrigger.lockOnX = LockOn.PLAYER
        Assertions.assertTrue(moveTrigger.lockOnPlayerX.getOrThrow())
        Assertions.assertFalse(moveTrigger.lockOnCameraX.getOrThrow())

        moveTrigger.lockOnX = LockOn.CAMERA
        Assertions.assertFalse(moveTrigger.lockOnPlayerX.getOrThrow())
        Assertions.assertTrue(moveTrigger.lockOnCameraX.getOrThrow())

        moveTrigger.lockOnX = LockOn.NONE
        Assertions.assertFalse(moveTrigger.lockOnPlayerX.getOrThrow())
        Assertions.assertFalse(moveTrigger.lockOnCameraX.getOrThrow())


        moveTrigger.lockOnPlayerY.value = true
        Assertions.assertEquals(LockOn.PLAYER, moveTrigger.lockOnY)

        moveTrigger.lockOnPlayerY.value = false
        moveTrigger.lockOnCameraY.value = true
        Assertions.assertEquals(LockOn.CAMERA, moveTrigger.lockOnY)

        moveTrigger.lockOnCameraY.value = false
        Assertions.assertEquals(LockOn.NONE, moveTrigger.lockOnY)

        moveTrigger.lockOnY = LockOn.PLAYER
        Assertions.assertTrue(moveTrigger.lockOnPlayerY.getOrThrow())
        Assertions.assertFalse(moveTrigger.lockOnCameraY.getOrThrow())

        moveTrigger.lockOnY = LockOn.CAMERA
        Assertions.assertFalse(moveTrigger.lockOnPlayerY.getOrThrow())
        Assertions.assertTrue(moveTrigger.lockOnCameraY.getOrThrow())

        moveTrigger.lockOnY = LockOn.NONE
        Assertions.assertFalse(moveTrigger.lockOnPlayerY.getOrThrow())
        Assertions.assertFalse(moveTrigger.lockOnCameraY.getOrThrow())
    }

    @Test
    fun colorTriggerTest() {
        val color = Color.CYAN
        val newColor = Color.DARK_GRAY

        val trigger = ColorTrigger(Pos.ZERO, color, 5u)

        Assertions.assertEquals(color, trigger.color)
        Assertions.assertEquals(color.red, trigger.red.getOrThrow().toInt())
        Assertions.assertEquals(color.green, trigger.green.getOrThrow().toInt())
        Assertions.assertEquals(color.blue, trigger.blue.getOrThrow().toInt())

        trigger.color = newColor
        Assertions.assertEquals(newColor, trigger.color)
        Assertions.assertEquals(newColor.red, trigger.red.getOrThrow().toInt())
        Assertions.assertEquals(newColor.green, trigger.green.getOrThrow().toInt())
        Assertions.assertEquals(newColor.blue, trigger.blue.getOrThrow().toInt())

        // Player color stuff

        Assertions.assertEquals(PlayerColor.NONE, trigger.playerColor)

        trigger.playerColor1.value = true
        Assertions.assertEquals(PlayerColor.PLAYER_1, trigger.playerColor)

        trigger.playerColor1.value = false
        trigger.playerColor2.value = true
        Assertions.assertEquals(PlayerColor.PLAYER_2, trigger.playerColor)

        trigger.playerColor1.resetValue()
        trigger.playerColor2.resetValue()
        Assertions.assertEquals(PlayerColor.NONE, trigger.playerColor)

        trigger.playerColor = PlayerColor.NONE
        Assertions.assertFalse(trigger.playerColor1.getOrThrow())
        Assertions.assertFalse(trigger.playerColor2.getOrThrow())

        trigger.playerColor = PlayerColor.PLAYER_1
        Assertions.assertTrue(trigger.playerColor1.getOrThrow())
        Assertions.assertFalse(trigger.playerColor2.getOrThrow())

        trigger.playerColor = PlayerColor.PLAYER_2
        Assertions.assertFalse(trigger.playerColor1.getOrThrow())
        Assertions.assertTrue(trigger.playerColor2.getOrThrow())
    }

    @Test
    fun speedPortalObjectTest() {
        val obj = SpeedPortalObject(Speed.entries.first(), 0f, 0f)
        Speed.entries.forEach {
            obj.speed = it

            Assertions.assertEquals(it.objID, obj.objID.getOrThrow())
            CustomAssertions.assertRawStringEquals("1,${it.objID},2,0.0,3,0.0", obj.asRawString())

            Assertions.assertEquals(it, obj.speed)
        }
    }
}

@Tag(TestTags.EDITOR)
private class AlternativeConstructorsTests {
    @Test
    fun moveTriggerTest() {
        Assertions.assertEquals(5u, MoveTrigger(0f, 0f, 5u).targetGroup.getOrThrow())
        Assertions.assertEquals(5u, MoveTrigger(Pos(0f, 0f), 5u).targetGroup.getOrThrow())

        Assertions.assertEquals(5u, DirectionMoveTrigger(0f, 0f, 5u).targetGroup.getOrThrow())
        Assertions.assertEquals(5u, DirectionMoveTrigger(Pos(0f, 0f), 5u).targetGroup.getOrThrow())

        Assertions.assertEquals(5u, TargetMoveTrigger(0f, 0f, 5u).targetGroup.getOrThrow())
        Assertions.assertEquals(5u, TargetMoveTrigger(Pos(0f, 0f), 5u).targetGroup.getOrThrow())
    }
}
