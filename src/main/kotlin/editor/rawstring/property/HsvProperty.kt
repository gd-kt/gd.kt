package editor.rawstring.property

import editor.objects.data.Hsv
import editor.rawstring.Id

class HsvProperty(id: Id, defaultValue: Hsv? = Hsv.create(), currentValue: Hsv? = null) : AbstractProperty<Hsv>(id, defaultValue, currentValue) {
    override val serializer = Hsv.SERIALIZER

    private var usesColorPropID: Id? = null

    /**
     * Sets the id of the property `uses[Base/Detail]ColorHSV` to be included in the raw string output
     * @param propID the property id of the `uses[Base/Detail]ColorHSV` property
     * @return the hsv property object, aka `this`
     */
    @Deprecated(message = "This is kept intact but conditional properties should be used in favor")
    fun setUsesColorProp(propID: Id): HsvProperty {
        this.usesColorPropID = propID
        return this
    }

    override fun asRawString(separator: Char): String {
        val suffix =
            if (this.usesColorPropID == null)
                ""
            else
                KEY_VAL_SEPARATOR + this.usesColorPropID!!.getID() + KEY_VAL_SEPARATOR + "1"

        return this.toRawStringHelper(Hsv.SERIALIZER, separator, suffix)
    }
}