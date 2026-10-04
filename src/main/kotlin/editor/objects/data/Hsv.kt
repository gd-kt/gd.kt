package editor.objects.data

import exceptions.InvalidRawStringException
import editor.rawstring.RawStringable
import editor.rawstring.serializing.Serializer
import utils.toBooleanFromIntStrict
import utils.toInt
import java.util.Objects

/**
 * Class used to store **H**ue **S**aturation **V**alues. *(see [wikipedia](https://en.wikipedia.org/wiki/HSL_and_HSV))*.
 * The values are clamped according to this table:
 *
 * | Name | Range (unchecked) | Range (checked) |
 * | --- | --- | --- |
 * | Hue | `[-180, 180]` | `[-180, 180]` |
 * | Saturation | `[0, 2]` | `[-1, 1]` |
 * | Brightness | `[0, 2]` | `[-1, 1]` |
 *
 * Checked values only are an offset, they don't affect the visual output.
 *
 * Do note that in geometry dash, the "value" field is the [brightness].
 * @see editor.rawstring.property.HsvProperty
 */
class Hsv : RawStringable {
    companion object {
        val SERIALIZER: Serializer<Hsv> = Serializer.fromRawstringable(Hsv::parseHsv)

        const val SEPARATOR: Char = 'a'
        val HUE_RANGE = -180..180
        val SATURATION_RANGE = HsvRange(0f..2f, -1f..1f)
        val BRIGHTNESS_RANGE = SATURATION_RANGE

        fun create(hue: Int = 0, saturation: Float = 1f, brightness: Float = 1f): Hsv =
            Hsv(hue, saturation, brightness)

        fun checkedSat(hue: Int = 0, saturation: Float = 0f, brightness: Float = 1f): Hsv =
            Hsv(hue, saturation, brightness, isSaturationChecked = true)

        fun checkedBrightness(hue: Int = 0, saturation: Float = 1f, brightness: Float = 0f): Hsv =
            Hsv(hue, saturation, brightness, isBrightnessChecked = true)

        fun checkedSatBrightness(hue: Int = 0, saturation: Float = 0f, brightness: Float = 0f): Hsv =
            Hsv(hue, saturation, brightness, isSaturationChecked = true, isBrightnessChecked = true)

        fun parseHsv(rawString: String): Hsv {
            try {
                val values = rawString.trim().split(SEPARATOR)
                return Hsv(
                    values[0].toInt(),
                    values[1].toFloat(),
                    values[2].toFloat(),
                    values[3].toBooleanFromIntStrict(),
                    values[4].toBooleanFromIntStrict()
                )
            } catch (e: RuntimeException) {
                throw InvalidRawStringException(rawString, e)
            }
        }
    }

    private var hueValue: Int
    private var saturationValue: Float
    private var brightnessValue: Float
    private var isSaturationCheckedValue: Boolean
    private var isBrightnessCheckedValue: Boolean

    var hue: Int
        get() = this.hueValue
        set(value) {
            this.hueValue = value.coerceIn(HUE_RANGE)
        }

    var sat: Float
        get() = this.saturationValue
        set(value) {
            this.saturationValue = SATURATION_RANGE.clamp(value, this.isSaturationCheckedValue)
        }

    var brightness: Float
        get() = this.brightnessValue
        set(value) {
            this.brightnessValue = BRIGHTNESS_RANGE.clamp(value, this.isBrightnessCheckedValue)
        }

    var isSatChecked: Boolean
        get() = this.isSaturationCheckedValue
        set(value) {
            if (value != this.isSaturationCheckedValue) {
                val offset = if (value) -1 else 1
                this.saturationValue += offset
            }

            this.isSaturationCheckedValue = value

//            this.saturationValue = SATURATION_RANGE.clamp(this.saturationValue, this.isSaturationCheckedValue)
//            this.brightnessValue = BRIGHTNESS_RANGE.clamp(this.brightnessValue, this.isBrightnessCheckedValue)
        }

    var isBrightnessChecked: Boolean
        get() = this.isBrightnessCheckedValue
        set(value) {
            if (value != this.isBrightnessCheckedValue) {
                val offset = if (value) -1 else 1
                this.brightnessValue += offset
            }

            this.isBrightnessCheckedValue = value

//            this.saturationValue = SATURATION_RANGE.clamp(this.saturationValue, this.isSaturationCheckedValue)
//            this.brightnessValue = BRIGHTNESS_RANGE.clamp(this.brightnessValue, this.isBrightnessCheckedValue)
        }

    constructor(
        hue: Int,
        saturation: Float,
        brightness: Float,
        isSaturationChecked: Boolean = false,
        isBrightnessChecked: Boolean = false
    ) {
        this.hueValue = hue
        this.saturationValue = saturation
        this.brightnessValue = brightness
        this.isSaturationCheckedValue = isSaturationChecked
        this.isBrightnessCheckedValue = isBrightnessChecked

        // Clamping the values
        this.hueValue = this.hueValue.coerceIn(HUE_RANGE)
        this.saturationValue = SATURATION_RANGE.clamp(this.saturationValue, this.isSatChecked)
        this.brightnessValue = BRIGHTNESS_RANGE.clamp(this.brightnessValue, this.isBrightnessChecked)
    }

    constructor(
        hue: Int = 0,
        saturation: Float = 1f,
        brightness: Float = 1f
    ) : this(hue, saturation, brightness, false, false)

    override fun asRawString(): String =
        arrayOf<Number>(this.hue, this.sat, this.brightness, this.isSatChecked.toInt(), this.isBrightnessChecked.toInt())
            .joinToString(SEPARATOR.toString())

    override fun equals(other: Any?): Boolean =
        other is Hsv && this.asRawString() == other.asRawString()

    override fun hashCode(): Int {
        return Objects.hash(this.hueValue, this.saturationValue, this.brightnessValue, this.isSaturationCheckedValue, this.isBrightnessCheckedValue)
    }

    private fun getName(): String {
        val className = this::class.simpleName!!
        return when {
            this.isSaturationCheckedValue && this.isBrightnessCheckedValue -> "$className / checked sat+brightness"
            this.isSaturationCheckedValue -> "$className / checked sat"
            this.isBrightnessCheckedValue -> "$className / checked brightness"
            else -> className
        }
    }

    override fun toString(): String {
        return "${this.getName()}(hue = ${this.hueValue}, sat = ${this.saturationValue}, brightness = ${this.brightnessValue})"
    }

    data class HsvRange(
        val default: ClosedFloatingPointRange<Float>,
        val whenChecked: ClosedFloatingPointRange<Float>,
    ) {
        fun get(isChecked: Boolean): ClosedFloatingPointRange<Float> =
            if (isChecked)
                this.whenChecked
            else
                this.default

        fun clamp(value: Float, isChecked: Boolean): Float = value.coerceIn(this.get(isChecked))
    }
}
