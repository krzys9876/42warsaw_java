package org.example

import scala.annotation.tailrec

@main
def main(): Unit =
  val game = GameOfLife(Counter(), Board(Coord[Row](15), Coord[Col](25))).init
  game.run()


case class GameOfLife(counter: Counter, board: Board):
  @tailrec
  final def run(): GameOfLife =
    board.draw()
    counter.draw()
    Thread.sleep(250)
    nextGen().run()

  private def nextGen(): GameOfLife =
    copy(counter = counter.inc, board = board.cycle)

  def init: GameOfLife =
    val boardWithGliders = board.makeGlider(Coord[Row](5), Coord[Col](2)).makeGlider(Coord[Row](8), Coord[Col](14))
    copy(board = boardWithGliders)


case class Counter(value: Int):
  def inc: Counter = Counter(value + 1)
  override def toString: String = value.toString
  def draw(): Unit = println(toString)

object Counter:
  def apply(): Counter = Counter(0)


case class Cell(value: Boolean, neighbours: Neighbours, row: Coord[Row], col: Coord[Col]):
  def set: Cell = copy(value = true)
  private def reset: Cell = copy(value = false)
  def addNeighbour(): Cell = copy(neighbours = neighbours.inc)
  def resetNeighbours: Cell = copy(neighbours = Neighbours(0))

  def nextGeneration: Cell =
    (value, neighbours) match {
      case (true, Neighbours(n)) if n < 2 => reset // rule 1
      case (true, Neighbours(n)) if n == 2 || n == 3 => this // rule 2
      case (true, Neighbours(n)) if n > 3 => reset // rule 3
      case (false, Neighbours(n)) if n == 3 => set // rule 4
      case _ => this
    }

  override def toString: String = if (value) "X " else ". "


case class Neighbours(value: Int):
  def inc: Neighbours = Neighbours(value + 1)


//NOTE: these are empty types, used only to tag coords, not to bring any functionality
sealed trait Row
sealed trait Col

case class Coord[T](value: Int):
  def inc: Coord[T] = Coord[T](value + 1)
  def +(addValue: Coord[T]) = Coord[T](value + addValue.value)
  def %(modValue: Coord[T]) = Coord[T](Coords.mod(value, modValue.value))


case class Coords(row: Coord[Row], col: Coord[Col]) {}

object Coords:
  def mod(value: Int, modValue: Int): Int = (value + modValue) % modValue

case class Board(rows: Coord[Row], cols: Coord[Col], cells: List[Cell]):
  private def getText = cells.map(_.toString).sliding(cols.value, cols.value).map(l => l.mkString("")).toList.mkString("\n")

  def draw(): Unit =
    print("\u001B[H\u001B[2J")
    println(getText)

  def cycle: Board =
    val cellsWithNeighbours = cells.map(cell =>
      Board.neighbourhood.foldLeft(cell.resetNeighbours)((c, n) =>
        if(isSetAt(c.row+n.row, c.col+n.col)) c.addNeighbour() else c))
    copy(cells = cellsWithNeighbours.map(_.nextGeneration))

  private def indexAt(row: Coord[Row], col: Coord[Col]): Int = (row % rows).value * cols.value + (col % cols).value
  private def cellAt(row: Coord[Row], col: Coord[Col]): Cell = cells(indexAt(row, col))
  private def isSetAt(row: Coord[Row], col: Coord[Col]): Boolean = cellAt(row, col).value
  private def setAt(row: Coord[Row], col: Coord[Col]): Board =
    val index = indexAt(row, col)
    copy(cells = cells.updated(index, cells(index).set))

  def makeGlider(row: Coord[Row], col: Coord[Col]): Board =
    setAt(row, col + Coord[Col](1))
      .setAt(row + Coord[Row](1), col + Coord[Col](2))
      .setAt(row + Coord[Row](2), col)
      .setAt(row + Coord[Row](2), col + Coord[Col](1))
      .setAt(row + Coord[Row](2), col + Coord[Col](2))


object Board:
  private val neighbourhood: List[Coords] =
    (-1 to 1).foldLeft(List[Coords]())((lr, r) =>
      (-1 to 1).foldLeft(lr)((lc, c)=>
        if(!(r==0 && c==0)) lc :+ Coords(Coord[Row](r), Coord[Col](c)) else lc))

  def apply(rows: Coord[Row], cols: Coord[Col]): Board =
    val cells = (0 until rows.value * cols.value).foldLeft(List[Cell]())((c, v)=>
      c :+ Cell(false, Neighbours(0), Coord[Row](v / cols.value), Coord[Col](v % cols.value)))
    new Board(rows, cols, cells)

