package editor.objects.triggers.counters

import annotations.GDName
import editor.objects.data.Pos
import editor.objects.data.Position
import editor.objects.triggers.TriggerObject
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.BoolProperty
import editor.rawstring.property.UIntProperty
import editor.rawstring.property.getOrThrow

/**
 * A persistent trigger allows to set an item ID as persistent.
 * This means that it will only reset on **level exit** and **not on the player's death**.
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 */
class PersistentTrigger : TriggerObject {
    companion object {
        const val OBJ_ID = 3641u
    }

    val itemID = UIntProperty(80.id)
    /**
     * If the [itemID] is a timer
     */
    @GDName("Timer")
    val isTimer = BoolProperty(494.id)

    /**
     * Sets the given [itemID] as persistent
     */
    @GDName("Persistent")
    val setPersistent = BoolProperty(491.id)

    @GDName("Target All")
    val targetAllPersistentItems = BoolProperty(492.id)
    @GDName("Reset")
    val resetSelectedItems = BoolProperty(493.id)


    constructor(pos: Position) : super(OBJ_ID, pos)
    constructor(x: Float, y: Float) : super(OBJ_ID, x, y)

    constructor(pos: Position, itemID: UInt, setPersistent: Boolean = false) : this(pos) {
        this.itemID.value = itemID
        this.setPersistent.value = setPersistent
    }
    constructor(x: Float, y: Float, itemID: UInt, setPersistent: Boolean = false) : this(Pos(x, y), itemID, setPersistent)
}

/**
 * Sets [itemID] to be persistent
 * @param itemID the item ID to set as persistent
 * @param isTimer if the item ID contains a timer
 * @return `this`
 */
fun PersistentTrigger.setPersistent(itemID: UInt = this.itemID.getOrThrow(), isTimer: Boolean? = this.isTimer.value): PersistentTrigger {
    this.resetSelectedItems.resetValue()
    this.targetAllPersistentItems.resetValue()

    this.itemID.value = itemID
    this.setPersistent.value = true
    if (isTimer != null)
        this.isTimer.value = isTimer

    return this
}

/**
 * Resets [itemID] to `0` *(even if the given [itemID] is not a persistent one)*
 * @param itemID the item ID to reset
 * @return `this`
 */
fun PersistentTrigger.resetItem(itemID: UInt = this.itemID.getOrThrow()): PersistentTrigger {
    this.targetAllPersistentItems.resetValue()
    this.setPersistent.resetValue()
    this.isTimer.resetValue()

    this.itemID.value = itemID
    this.resetSelectedItems.value = true

    return this
}

/**
 * Resets all persistent item IDs to `0`
 * @return `this`
 */
fun PersistentTrigger.resetAll(): PersistentTrigger {
    this.resetSelectedItems.value = true
    this.targetAllPersistentItems.value = true

    this.setPersistent.resetValue()
    this.isTimer.resetValue()
    this.itemID.resetValue()

    return this
}
