package editor.objects.triggers.collision

import annotations.GDName
import editor.objects.data.Position
import editor.objects.triggers.TriggerObject
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.BoolProperty
import editor.rawstring.property.MutableConditionalProperty
import editor.rawstring.serializing.Serializers

/**
 * A collision trigger allows to check if a [Collision Block][CollisionBlockObject] has collided with another [Collision Block][CollisionBlockObject].
 * Unlike the [CollisionTrigger], the collision check is done once per spawning, and a "false" and "true" output can be chosen
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 * @see CollisionTrigger
 * @see CollisionBlockObject
 */
class InstantCollisionTrigger : TriggerObject {
    companion object {
        const val OBJ_ID = 3609u
    }

    /**
     * The group id to spawn if the check **succeeded**
     */
    @GDName("True ID")
    val succeededSpawnID = BoolProperty(51.id)
    /**
     * The group id to spawn if the check **failed**
     */
    @GDName("False ID")
    val failedSpawnID = BoolProperty(71.id)

    @GDName("PP")
    val isPlayerCollideCheck = BoolProperty(201.id)

    @GDName("P1")
    val blockAisP1 = MutableConditionalProperty.createIndependent(138.id, defaultValue = false, serializer = Serializers.BOOLEAN) {
        !this.isPlayerCollideCheck.isSerializable()
    }

    @GDName("P2")
    val blockAisP2 = MutableConditionalProperty.createIndependent(200.id, defaultValue = false, serializer = Serializers.BOOLEAN) {
        !this.isPlayerCollideCheck.isSerializable()
    }

    val blockA = MutableConditionalProperty.createIndependent(80.id, defaultValue = 0u, serializer = Serializers.UINT) {
        !this.blockAisP1.isSerializable() && !this.blockAisP2.isSerializable() && !this.isPlayerCollideCheck.isSerializable()
    }
    val blockB = MutableConditionalProperty.createIndependent(95.id, defaultValue = 0u, serializer = Serializers.UINT) {
        !this.isPlayerCollideCheck.isSerializable()
    }

    constructor(pos: Position) : super(OBJ_ID, pos)
    constructor(x: Float, y: Float) : super(OBJ_ID, x, y)
}