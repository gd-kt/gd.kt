package editor.objects

import annotations.GDName
import editor.objects.data.Pos
import editor.objects.data.Position
import editor.objects.data.Scale
import editor.rawstring.DynamicRawStringFactory
import editor.rawstring.RawStringFactory
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.*
import editor.rawstring.serializing.Serializers

/**
 * A simple object is a generic geometry dash object in the editor.
 * This is the class that represents every other objects.
 *
 * **If you are wondering what any of these properties mean, check the [GD Editor Guide](https://www.robtopgames.com/files/GDEditor.pdf) !**
 */
open class SimpleObject : GenericGdObject {
    override val rawStringFactory: DynamicRawStringFactory = RawStringFactory.create(this)

    val objID = UIntProperty(1.id, defaultValue = null)
    val x = FloatProperty(2.id, defaultValue = null)
    val y = FloatProperty(3.id, defaultValue = null)
    val rotation = FloatProperty(6.id)
    // This is now deprecated because in 2.2
    // the editor uses scaleX and scaleY
    // val scale = FloatProperty(32u)
    val scaleX = FloatProperty(128.id, defaultValue = 1f)
    val scaleY = FloatProperty(129.id, defaultValue = 1f)
    val flipHorizontal = BoolProperty(5.id, defaultValue = false)
    val flipVertical = BoolProperty(4.id, defaultValue = false)
    val warpXangle = FloatProperty(132.id, defaultValue = 1f)
    val warpYangle = FloatProperty(131.id, defaultValue = 1f)
    val baseColorID = UIntProperty(21.id)
    val detailColorID = UIntProperty(22.id)
    val baseColorHSV = HsvProperty(43.id)
    val detailColorHSV = HsvProperty(44.id)
    val usesBaseColorHSV =
        ConditionalProperty(41.id, this.baseColorHSV, Serializers.BOOLEAN, { it.isSerializable() }) { true }
    val usesDetailColorHSV =
        ConditionalProperty(42.id, this.detailColorHSV, Serializers.BOOLEAN, { it.isSerializable() }) { true }

    @GDName("Pink groups")
    val groupsParent = SequencedSetProperty(274.id, elemSerializer = Serializers.UINT)
    val groups = GroupSetProperty(57.id, groupParentsProperty = this.groupsParent)
    val editorLayer = IntProperty(20.id)
    val editorLayer2 = IntProperty(61.id)
    val zOrder = IntProperty(25.id, defaultValue = null)
    @GDName("ORD")
    val order = IntProperty(115.id)
    @GDName("CH")
    val channel = IntProperty(170.id)
    // TODO: Make a object class where the 'preview' property is a thing
    val linkedGroupID = IntProperty(108.id)

    // val customProperties = PropertiesSet<ImplementableProperty<Any>>()

    inline var pos: Pos
        get() = Pos(this.x.value!!, this.y.value!!)
        set(value) {
            this.x.value = value.x
            this.y.value = value.y
        }

    inline var scale: Scale
        get() = Scale(this.scaleX.value!!, this.scaleY.value!!)
        set(value) {
            this.scaleX.value = value.width
            this.scaleY.value = value.height
        }

    constructor(objID: UInt, pos: Position) {
        this.objID.value = objID.coerceAtLeast(1u)
        this.setPos(pos)
    }

    constructor(objID: UInt, x: Float, y: Float) : this(objID, Pos(x, y))

    fun setScale(width: Float, height: Float) {
        this.scaleX.value = width
        this.scaleY.value = height
    }

    fun setPos(x: Float, y: Float) {
        this.x.value = x
        this.y.value = y
    }

    fun setPos(pos: Position) {
        this.x.value = pos.actualX
        this.y.value = pos.actualY
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleObject) return false

        return this.asRawString() == other.asRawString()
    }

    override fun hashCode(): Int =
        this.asRawString().hashCode()

    override fun toString(): String {
        return "${this::class.simpleName}(objID = ${this.objID.value}, x = ${this.x.value}, y = ${this.y.value})"
    }

// See why this was removed in RawStringable
//    override fun <T> set(propID: UInt, value: T) {
//        @Suppress("UNCHECKED_CAST")
//        val prop = this[propID] as AbstractProperty<T>
//        prop.value = value
//    }
}

/**
 * For more information see [RawStringFactory.asRawStringIntMap]
 * @see RawStringFactory.asRawStringIntMap
 */
fun SimpleObject.asRawStringMap(): Map<UInt, String> =
    this.rawStringFactory.asRawStringIntMap()

/**
 * For more information see [RawStringFactory.asIntMap]
 * @see RawStringFactory.asIntMap
 */
fun SimpleObject.asMap(): Map<UInt, PropertyDefinition<*>> =
    this.rawStringFactory.asIntMap()