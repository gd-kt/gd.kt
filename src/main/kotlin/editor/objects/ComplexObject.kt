package editor.objects

import annotations.GDName
import editor.objects.data.Position
import editor.objects.data.enums.SingleColorType
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.BoolProperty
import editor.rawstring.property.EnumProperty
import editor.rawstring.property.IntProperty
import editor.rawstring.serializing.Serializer

/**
 * A complex object, as the name suggests is "more complex".
 * Complex objects store properties which are present inside the `extra` and `extra2` tabs in the
 * "Edit Group" tab.
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 */
open class ComplexObject : SimpleObject {
    val dontFade = BoolProperty(64.id, defaultValue = false)
    val dontEnter = BoolProperty(64.id, defaultValue = false)
    val noEffects = BoolProperty(116.id, defaultValue = false)
    val groupParent = BoolProperty(34.id, defaultValue = false)
    val areaParent = BoolProperty(279.id, defaultValue = false)
    val dontBoostX = BoolProperty(496.id, defaultValue = false)
    val dontBoostY = BoolProperty(509.id, defaultValue = false)
    @GDName("singlePTouch")
    val singlePlayerTouch = BoolProperty(280.id, defaultValue = false)
    val highDetail = BoolProperty(103.id, defaultValue = false)
    val noTouch = BoolProperty(121.id, defaultValue = false)
    val passable = BoolProperty(134.id, defaultValue = false)
    val hide = BoolProperty(135.id, defaultValue = false)
    val nonStickX = BoolProperty(136.id, defaultValue = false)
    val nonStickY = BoolProperty(289.id, defaultValue = false)
    val extraSticky = BoolProperty(495.id, defaultValue = false)
    val extendedCollisions = BoolProperty(511.id, defaultValue = false)
    val centerEffect = BoolProperty(369.id, defaultValue = false)
    val iceBlock = BoolProperty(137.id, defaultValue = false)
    val gripSlop = BoolProperty(193.id, defaultValue = false)
    val noGlow = BoolProperty(96.id, defaultValue = false)
    val noParticle = BoolProperty(507.id, defaultValue = false)
    val scaleStick = BoolProperty(356.id, defaultValue = false)
    val noAudioScale = BoolProperty(372.id, defaultValue = false)
    val reverse = BoolProperty(117.id, defaultValue = false)

    val enterChannel = IntProperty(343.id)
    val material = IntProperty(446.id)
    val controlID = IntProperty(534.id)

    val singleColorType = EnumProperty(497.id, Serializer.enum(SingleColorType.entries))

    constructor(objID: UInt, pos: Position) : super(objID, pos)
    constructor(objID: UInt, x: Float, y: Float) : super(objID, x, y)
}