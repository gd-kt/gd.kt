package editor.rawstring

/**
 * An id is a way to store an identifier in a numerical or string type.
 * Numerical types are [unsigned integers][UInt] because in geometry dash, property ids don't go below `1`.
 *
 * You can create an [Id] instance, either by:
 * - In **java**: Using [ofString][String.id] and [ofInt][Int.id]
 * - In **kotlin**: Using the 3 extensions [Int.id], [UInt.id] and [String.id]
 */
@ConsistentCopyVisibility
data class Id private constructor(val numericalID: UInt?, val stringID: String?): Comparable<Id> {
    companion object {
        /**
         * Creates a numerical or string [Id] object.
         * It tries converting your [id] to an int, and depending on if it fails
         * or not it will return an id with the correct underlying [type]
         * @throws IllegalArgumentException if the given [id] is below `0` (exclusive, so `< 0`)
         */
        @JvmStatic
        fun ofUnknown(id: String): Id {
            val asUInt = id.toUIntOrNull()
            return if (asUInt == null) {
                val asInt = id.toIntOrNull()
                if (asInt == null) {
                    id.id
                } else {
                    throw IllegalArgumentException("Id argument (= $id) isn't a valid UInt", NumberFormatException("Invalid number format: '$id'"))
                }
            } else {
                asUInt.id
            }
        }

        /**
         * Creates a numerical ID for this integer
         * @see UInt.id
         */
        @get:JvmName("ofInt")
        @JvmStatic
        inline val Int.id: Id
            get() = this.coerceAtLeast(1).toUInt().id


        /**
         * Creates a numerical ID for this unsigned integer
         */
        @get:JvmName("ofUInt")
        @JvmStatic
        val UInt.id: Id
            get() = Id(this.coerceAtLeast(1u), null)

        /**
         * Creates a string ID for this string
         */
        @get:JvmName("ofString")
        @JvmStatic
        val String.id: Id
            get() = Id(null, this)
    }

    val type: Type
        get() =
            if (this.numericalID == null)
                Type.STRING
            else
                Type.NUMERICAL

    /**
     * Gets the [numerical id][numericalID] of this ID.
     * However, if it's `null` an exception gets thrown
     * @return the [numerical id][numericalID] of this ID
     * @throws NullPointerException if the [numerical id][numericalID] is `null`
     */
    fun getNumericalIdStrict(): UInt {
        if (this.numericalID == null)
            throw NullPointerException("Tried getting numerical id $this but failed because this id object is a string id and not a numerical id.")

        return this.numericalID
    }

    /**
     * Gets the [string id][stringID] of this ID.
     * However, if it's `null` an exception gets thrown
     * @return the [string id][stringID] of this ID
     * @throws NullPointerException if the [string id][stringID] is `null`
     */
    fun getStringIdStrict(): String {
        if (this.stringID == null)
            throw NullPointerException("Tried getting string id $this but failed because this id object is a numerical id and not a string id.")

        return this.stringID
    }

    /**
     * Gets the string representation of this id.
     * It chooses between the numerical id and the string one
     */
    fun getID(): String =
        if (this.numericalID == null)
            this.stringID!!
        else
            this.numericalID.toString()

    override fun compareTo(other: Id): Int {
        if (this.type == Type.STRING) {
            return if (other.type == Type.STRING) {
                // this: STRING ; other: STRING
                this.getStringIdStrict().compareTo(other.getStringIdStrict())
            } else {
                // this: STRING ; other: NUMERICAL
                this.getStringIdStrict().compareTo(other.getID())
            }
        } else {
            return if (other.type == Type.STRING) {
                // this: NUMERICAL ; other: STRING
                this.getID().compareTo(other.getStringIdStrict())
            } else {
                // this: NUMERICAL ; other: NUMERICAL
                this.getNumericalIdStrict().compareTo(other.getNumericalIdStrict())
            }
        }
    }

    /**
     * Returns a string representation of the object.
     * If you are looking to get the **string representation** of the id, use [getID]
     */
    override fun toString(): String = this.getID()

    enum class Type {
        NUMERICAL,
        STRING
    }
}
