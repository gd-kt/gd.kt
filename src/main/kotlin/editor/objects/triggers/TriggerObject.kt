package editor.objects.triggers

import editor.objects.ComplexObject
import editor.objects.data.Position
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.BoolProperty
import editor.rawstring.property.MutableConditionalProperty
import editor.rawstring.serializing.Serializers

/**
 * A trigger object is the base class for every trigger.
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 */
abstract class TriggerObject : ComplexObject {
    val spawnTriggered = BoolProperty(62.id, false)
    val touchTriggered = BoolProperty(11.id, false)
    val multiTriggered =
        MutableConditionalProperty.createIndependent(87.id, defaultValue = false, serializer = Serializers.BOOLEAN) {
            this@TriggerObject.spawnTriggered.isSerializable() || this@TriggerObject.touchTriggered.isSerializable()
        }

    constructor(objID: UInt, pos: Position) : super(objID, pos)
    constructor(objID: UInt, x: Float, y: Float) : super(objID, x, y)
}