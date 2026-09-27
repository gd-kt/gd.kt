package editor.objects.triggers.collision

import annotations.GDName
import editor.objects.ComplexObject
import editor.objects.data.Pos
import editor.objects.data.Position
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.BoolProperty
import editor.rawstring.property.UIntProperty

/**
 * A collision block allows to check whenever another block collided with this one
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 * @see InstantCollisionTrigger
 * @see CollisionTrigger
 */
class CollisionBlockObject : ComplexObject {
    companion object {
        const val OBJ_ID = 1816u
    }

    val blockID = UIntProperty(80.id)

    /**
     * If set to `true` it will run collision checks with nearby
     * collision blocks.
     *
     * Used with collision triggers.
     */
    @GDName("Dynamic Block")
    val isDynamicBlock = BoolProperty(94.id)

    constructor(pos: Position) : super(OBJ_ID, pos)
    constructor(x: Float, y: Float) : super(OBJ_ID, x, y)

    constructor(pos: Position, blockID: UInt, isDynamicBlock: Boolean = false) : this(pos) {
        this.blockID.value = blockID
        this.isDynamicBlock.value = isDynamicBlock
    }
    constructor(x: Float, y: Float, blockID: UInt, isDynamicBlock: Boolean = false) : this(Pos(x, y), blockID, isDynamicBlock)
}