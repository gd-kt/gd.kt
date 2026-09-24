package editor.objects.data

sealed interface Position {
    companion object {
        const val GRID_UNIT: Int = 30
        const val GRID_OFFSET: Int = 15
    }

    /**
     * The actual x position of this position object.
     *
     * This is useful for [GridPos] because its position fields ([x][GridPos.x] / [y][GridPos.y])
     * don't actually represent the true geometry dash position
     */
    val actualX: Float

    /**
     * The actual y position of this position object.
     *
     * This is useful for [GridPos] because its position fields ([x][GridPos.x] / [y][GridPos.y])
     * don't actually represent the true geometry dash position
     */
    val actualY: Float
}

sealed interface OperatorPosition<T : OperatorPosition<T>> : Position {
    operator fun times(num: Float): T

    operator fun times(pos: Position): T

    operator fun div(num: Float): T

    operator fun div(pos: Position): T

    operator fun plus(num: Float): T

    operator fun plus(pos: Position): T

    operator fun minus(num: Float): T

    operator fun minus(pos: Position): T
}

/**
 * The position for a geometry dash object.
 * 1 `grid unit` = 30 `space units`
 */
data class Pos(
    val x: Float,
    val y: Float
) : OperatorPosition<Pos> {
    companion object {
        @JvmField
        @get:JvmName("ZERO")
        val ZERO: Pos = Pos()

        /**
         * Create a [Pos] object on the intersections of geometry dash's grid
         * @return the [Pos] object with the grid coordinates
         * @see gridCentered
         */
        @JvmStatic
        fun gridUncentered(x: Float, y: Float): Pos =
            Pos(x.gridUncentered, y.gridUncentered)

        /**
         * Create a [Pos] object on geometry dash's grid
         * @return the [Pos] object with the grid coordinates
         * @see gridUncentered
         */
        @JvmStatic
        fun gridCentered(x: Float, y: Float): Pos =
            Pos(x.gridCentered, y.gridCentered)

        @JvmStatic
        fun ofPos(pos: Position): Pos =
            Pos(pos.actualX, pos.actualY)
    }

    /**
     * Creates an empty [Pos] at the coordinates `(0, 0)`
     * @see ZERO
     */
    constructor() : this(0f, 0f)

    override operator fun times(num: Float): Pos =
        this.copy(x = this.x * num, y = this.y * num)

    override operator fun times(pos: Position): Pos =
        this.copy(x = this.x * pos.actualX, y = this.y * pos.actualY)

    override operator fun div(num: Float): Pos =
        this.copy(x = this.x / num, y = this.y / num)

    override operator fun div(pos: Position): Pos =
        this.copy(x = this.x / pos.actualX, y = this.y / pos.actualY)

    override operator fun plus(num: Float): Pos =
        this.copy(x = this.x + num, y = this.y + num)

    override operator fun plus(pos: Position): Pos =
        this.copy(x = this.x + pos.actualX, y = this.y + pos.actualY)

    override operator fun minus(num: Float): Pos =
        this.copy(x = this.x - num, y = this.y - num)

    override operator fun minus(pos: Position): Pos =
        this.copy(x = this.x - pos.actualX, y = this.y - pos.actualY)

    override val actualX = this.x
    override val actualY = this.y

    val gridPos: GridPos
        get() = GridPos.ofPos(this)
}

/**
 * The position for a geometry dash object based on the gd editor's grid
 * 1 `grid unit` = `30`
 */
data class GridPos(
    val x: Float,
    val y: Float
) : OperatorPosition<GridPos> {
    companion object {
        @JvmField
        @get:JvmName("ZERO")
        val ZERO: GridPos = GridPos()

        /**
         * Create a [GridPos] object on the intersections of geometry dash's grid
         * @return the [GridPos] object with the grid coordinates
         * @see gridCentered
         */
        @JvmStatic
        fun gridUncentered(x: Float, y: Float): GridPos =
            GridPos(x, y)

        /**
         * Create a [GridPos] object on geometry dash's grid
         * @return the [GridPos] object with the grid coordinates
         * @see gridUncentered
         */
        @JvmStatic
        fun gridCentered(x: Float, y: Float): GridPos =
            GridPos(x + Position.GRID_OFFSET.toFloat() / Position.GRID_UNIT, y + Position.GRID_OFFSET.toFloat() / Position.GRID_UNIT)

        @JvmStatic
        fun ofPos(pos: Position): GridPos =
            GridPos(pos.actualX / Position.GRID_UNIT, pos.actualY / Position.GRID_UNIT)
    }

    /**
     * Creates an empty [GridPos] at the coordinates `(0, 0)`
     * @see ZERO
     */
    constructor() : this(0f, 0f)

    override operator fun times(num: Float): GridPos =
        this.copy(x = this.x * num, y = this.y * num)

    override operator fun times(pos: Position): GridPos =
        this.copy(x = this.x * pos.actualX, y = this.y * pos.actualY)

    override operator fun div(num: Float): GridPos =
        this.copy(x = this.x / num, y = this.y / num)

    override operator fun div(pos: Position): GridPos =
        this.copy(x = this.x / pos.actualX, y = this.y / pos.actualY)

    override operator fun plus(num: Float): GridPos =
        this.copy(x = this.x + num, y = this.y + num)

    override operator fun plus(pos: Position): GridPos =
        this.copy(x = this.x + pos.actualX, y = this.y + pos.actualY)

    override operator fun minus(num: Float): GridPos =
        this.copy(x = this.x - num, y = this.y - num)

    override operator fun minus(pos: Position): GridPos =
        this.copy(x = this.x - pos.actualX, y = this.y - pos.actualY)

    override val actualX = this.x * Position.GRID_UNIT
    override val actualY = this.y * Position.GRID_UNIT

    val gdPos: Pos
        get() = Pos.ofPos(this)
}

/**
 * Get the grid value relative to geometry dash's position units where
 * 1 `grid unit` = `30`
 *
 * This is a quick way to multiply this value by `30`
 * @see offsetGrid
 * @see Pos.gridUncentered
 */
inline val Int.grid: Int
    get() = this * Position.GRID_UNIT

/**
 * Get the grid value relative to geometry dash's position units where
 * 1 `grid unit` = `30` and center it on the grid
 *
 * This is equal to `val * 30 + 15`
 * @see grid
 * @see Pos.gridCentered
 */
inline val Int.offsetGrid: Int
    get() = this.grid + Position.GRID_OFFSET


/**
 * Get the grid value relative to geometry dash's position units where
 * 1 `grid unit` = `30`
 *
 * This is a quick way to multiply this value by `30`
 * @see gridCentered
 * @see Pos.gridUncentered
 */
inline val Float.gridUncentered: Float
    get() = this * Position.GRID_UNIT

/**
 * Get the grid value relative to geometry dash's position units where
 * 1 `grid unit` = `30` and center it on the grid
 *
 * This is equal to `val * 30 + 15`
 * @see gridUncentered
 * @see Pos.gridCentered
 */
inline val Float.gridCentered: Float
    get() = this.gridUncentered + Position.GRID_OFFSET
