package org.example

fun main() {
    GameOfLife.run(GameOfLife(board = Board(CoordRow(15), CoordCol(25))).init())
}


data class GameOfLife(val counter: Counter = Counter(), val board: Board) {
    private fun nextGen(): GameOfLife =
        copy(counter = counter.inc(), board = board.cycle())

    fun init(): GameOfLife {
        val boardWithGliders = board
            .makeGlider(CoordRow(5), CoordCol(2))
            .makeGlider(CoordRow(8), CoordCol(14))
        return copy(board = boardWithGliders)
    }

    companion object {
        tailrec fun run(game: GameOfLife): GameOfLife {
            game.board.draw()
            game.counter.draw()
            Thread.sleep(250)
            return run(game.nextGen())
        }
    }
}


data class Counter(val value: Int = 0) {
    fun inc(): Counter = Counter(value + 1)
    override fun toString(): String = value.toString()
    fun draw() = println(this)
}


data class Cell(
    val value: Boolean,
    val neighbours: Neighbours,
    val row: CoordRow,
    val col: CoordCol,
) {
    fun set(): Cell = copy(value = true)
    private fun reset(): Cell = copy(value = false)
    fun addNeighbour(): Cell = copy(neighbours = neighbours.inc())
    fun resetNeighbours(): Cell = copy(neighbours = Neighbours(0))

    fun nextGeneration(): Cell =
        if (neighbours.value == 3 || (value && neighbours.value == 2)) set() else reset()

    override fun toString(): String = if (value) "X " else ". "
}


data class Neighbours(val value: Int) {
    fun inc(): Neighbours = Neighbours(value + 1)
}


@JvmInline
value class CoordRow(val value: Int) {
    operator fun plus(other: CoordRow): CoordRow = CoordRow(value + other.value)
    fun wrap(rows: CoordRow): CoordRow = CoordRow(((value % rows.value) + rows.value) % rows.value)
}

@JvmInline
value class CoordCol(val value: Int) {
    operator fun plus(other: CoordCol): CoordCol = CoordCol(value + other.value)
    fun wrap(cols: CoordCol): CoordCol = CoordCol(((value % cols.value) + cols.value) % cols.value)
}

data class Coords(val row: CoordRow, val col: CoordCol)


data class Board(val rows: CoordRow, val cols: CoordCol, val cells: List<Cell>) {
    private fun getText(): String =
        cells.chunked(cols.value).joinToString("\n") { row -> row.joinToString("") }

    fun draw() {
        print("\u001B[H\u001B[2J")
        println(getText())
    }

    fun cycle(): Board {
        val cellsWithNeighbours = cells.map { cell ->
            neighbourhood.fold(cell.resetNeighbours()) { c, n ->
                if (isSetAt(c.row + n.row, c.col + n.col)) c.addNeighbour() else c
            }
        }
        return copy(cells = cellsWithNeighbours.map { it.nextGeneration() })
    }

    private fun indexAt(row: CoordRow, col: CoordCol): Int =
        row.wrap(rows).value * cols.value + col.wrap(cols).value

    private fun cellAt(row: CoordRow, col: CoordCol): Cell = cells[indexAt(row, col)]
    private fun isSetAt(row: CoordRow, col: CoordCol): Boolean = cellAt(row, col).value

    private fun setAt(row: CoordRow, col: CoordCol): Board {
        val index = indexAt(row, col)
        return copy(cells = cells.mapIndexed { i, c -> if (i == index) c.set() else c })
    }

    fun makeGlider(row: CoordRow, col: CoordCol): Board =
        setAt(row, col + CoordCol(1))
            .setAt(row + CoordRow(1), col + CoordCol(2))
            .setAt(row + CoordRow(2), col)
            .setAt(row + CoordRow(2), col + CoordCol(1))
            .setAt(row + CoordRow(2), col + CoordCol(2))

    constructor(rows: CoordRow, cols: CoordCol) : this(
        rows,
        cols,
        (0 until rows.value * cols.value).map { v ->
            Cell(false, Neighbours(0), CoordRow(v / cols.value), CoordCol(v % cols.value))
        },
    )

    companion object {
        private val neighbourhood: List<Coords> =
            (-1..1).flatMap { r ->
                (-1..1).mapNotNull { c ->
                    if (r == 0 && c == 0) null else Coords(CoordRow(r), CoordCol(c))
                }
            }
    }
}
