# Game of Life — Java vs Scala vs Kotlin vs Clojure

Same program (Conway's Game of Life, two gliders on a 15×25 wrapping board),
four languages. Java is classic **mutable OO**. Scala and Kotlin keep the OO
*shape* but turn it **immutable** (data classes + `copy`) — they differ from
each other mostly in **syntax and ceremony**. Clojure differs in **paradigm** —
immutable data + pure functions + laziness, with the domain types deliberately
absent.

## At a glance

| Concept              | Java                                | Scala 3                          | Kotlin                               | Clojure                              |
|----------------------|-------------------------------------|----------------------------------|--------------------------------------|--------------------------------------|
| State model          | mutable `Board`/`Cell`/`Coord`      | `Board`/`Cell`/`Coord` classes   | same classes (`data class`)          | one `set` of `[row col]` vectors     |
| Value type           | plain class (getters, no `equals`)  | `case class`                     | `data class`                         | plain map/vector/set (no type)       |
| Mutability           | **mutates in place**                | immutable + `copy`               | immutable + `copy`                   | immutable persistent data            |
| Factory              | constructor(s)                      | companion `apply`                | default args / `operator fun invoke` | just a literal                       |
| Choice/rules         | `if / else if` chain                | `match` + extractors             | `when` + boolean guards              | `for ... :when`                      |
| Operators (`+`, `%`) | methods `plus` / `mod` (no overload)| method named `+` / `%`           | `operator fun plus` / `rem`          | ordinary `+` / `mod` functions       |
| The loop             | `while (true)` mutation             | `@tailrec` recursion             | `tailrec` recursion                  | `(iterate step initial)` lazy seq    |
| Counter              | mutable `Counter` (`inc()`)         | `Counter` class                  | `Counter` class                      | `map-indexed` over the seq           |
| Typing               | static, `CoordRow` ≠ `CoordCol`     | static, `CoordRow` ≠ `CoordCol`  | static, same                         | dynamic, untyped data                |
| Entry point          | `void main()` + `IO.println` (JEP)  | `@main def main()`               | top-level `fun main()`               | `(defn -main [& _] ...)`             |
| Run in IntelliJ      | built-in, ▶ run `main`              | sbt (built-in)                   | Gradle (bundled)                     | **Cursive plugin** + deps.edn        |

---

## 1. How state is modeled

**Java** — a graph of *mutable* typed objects: `Board` holds an `ArrayList<Cell>`,
each `Cell` has private mutable fields and setters.
```java
class Cell {
    private boolean value = false;
    private Neighbours neighbours = new Neighbours();
    private CoordRow row;
    private CoordCol col;
    void set()   { value = true; }   // mutates this cell
    void reset() { value = false; }
}
```

**Scala / Kotlin** — the same object graph, but *immutable*: `Board` holds
`List[Cell]`, each `Cell` knows its `value`, `Neighbours`, `CoordRow`, `CoordCol`,
and "changing" a cell returns a new one.

```scala
case class Cell(value: Boolean, neighbours: Neighbours, row: CoordRow, col: CoordCol)
case class Board(rows: CoordRow, cols: CoordCol, cells: List[Cell])
```
```kotlin
data class Cell(val value: Boolean, val neighbours: Neighbours,
                val row: CoordRow, val col: CoordCol)
data class Board(val rows: CoordRow, val cols: CoordCol, val cells: List<Cell>)
```

**Clojure** — there is no `Board` and no `Cell`. The world is the set of cells
that are alive; everything else is implied.
```clojure
(def initial (into (glider 5 2) (glider 8 14)))   ; => #{[5 3] [6 4] [7 2] ...}
```

## 2. The Game-of-Life rule

**Java** — nested `if / else if`, mutating the cell in place:
```java
void nextGeneration() {
    if (isSet()) {
        if (neighbours.getValue() < 2) reset();                                   // rule 1
        else if (neighbours.getValue() == 2 || neighbours.getValue() == 3) set(); // rule 2
        else if (neighbours.getValue() > 3) reset();                              // rule 3
    } else {
        if (neighbours.getValue() == 3) set();                                    // rule 4
    }
    neighbours.reset();
}
```

**Scala** — pattern match with extractors and guards:
```scala
def nextGeneration: Cell = (value, neighbours) match
  case (true,  Neighbours(n)) if n < 2           => reset // rule 1
  case (true,  Neighbours(n)) if n == 2 || n == 3 => set  // rule 2
  case (true,  Neighbours(n)) if n > 3           => reset // rule 3
  case (false, Neighbours(n)) if n == 3          => set   // rule 4
  case _                                          => reset
```

**Kotlin** — `when` with boolean conditions (no extractors, so no destructuring):
```kotlin
fun nextGeneration(): Cell = when {
    value && neighbours.value < 2     -> reset()
    value && neighbours.value in 2..3 -> set()
    value && neighbours.value > 3     -> reset()
    !value && neighbours.value == 3   -> set()
    else                              -> reset()
}
```

**Clojure** — the rule is expressed *over the whole board at once*, not per cell.
A cell lives if it was touched by exactly 3 live neighbours, or by 2 while alive:
```clojure
(defn step [live]
  (set
    (for [[cell n] (frequencies (mapcat neighbours live))
          :when (or (= n 3) (and (= n 2) (live cell)))]
      cell)))
```

## 3. Counting neighbours

**Java** — nested `forEach`, mutating each cell's neighbour counter in place:
```java
void cycle() {
    board.forEach(cell ->
        neighbourhood.forEach(neighbour -> {
            if (getAt(cell.row().plus(neighbour.row).mod(ROWS),
                      cell.col().plus(neighbour.col).mod(COLS)).isSet())
                cell.addNeighbour();
        }));
    board.stream().forEach(Cell::nextGeneration);
}
```

**Scala / Kotlin** — visit every cell of the grid, fold over the 8 offsets,
build a *new* cell with an incremented `Neighbours` counter:
```scala
val cellsWithNeighbours = cells.map(cell =>
  Board.neighbourhood.foldLeft(cell.resetNeighbours)((c, n) =>
    if isSetAt(c.row + n.row, c.col + n.col) then c.addNeighbour() else c))
```

**Clojure** — never visits empty space. Flat-map live cells to their neighbours,
then `frequencies` is the count:
```clojure
(frequencies (mapcat neighbours live))   ; => {[4 3] 1, [5 3] 2, ...}
```

## 4. The animation loop

**Java** — an imperative `while (true)` that mutates the board and counter:
```java
while (true) {
    drawBoard(counter);
    board.cycle();   // mutates board in place
    counter.inc();   // mutates counter in place
    Thread.sleep(250);
}
```

**Scala** — tail-recursive method that calls itself forever:
```scala
@tailrec final def run(): GameOfLife =
  board.draw(); counter.draw(); Thread.sleep(250); nextGen().run()
```
**Kotlin** — `tailrec` modifier, explicit `return`:
```kotlin
tailrec fun run(): GameOfLife {
    board.draw(); counter.draw(); Thread.sleep(250)
    return nextGen().run()
}
```
**Clojure** — generations are an *infinite lazy sequence*; the loop just walks it:
```clojure
(doseq [[gen live] (map-indexed vector (iterate step initial))]
  (print "\u001b[H\u001b[2J") (println (render live)) (println gen)
  (flush) (Thread/sleep 250))
```

## 5. Coordinates & typing

**Java** — `CoordRow`/`CoordCol` extend `Coord` (distinct types, like Scala/Kotlin),
but there is no operator overloading, so arithmetic is spelled out as `plus`/`mod`
methods with covariant return types:
```java
class CoordRow extends Coord {
    CoordRow(int value) { super(value); }
    CoordRow plus(Coord addValue) { return new CoordRow(super.plus(addValue)); }
    CoordRow mod(Coord modValue)  { return new CoordRow(super.mod(modValue)); }
}
```

**Scala / Kotlin** — `CoordRow` and `CoordCol` are *distinct types*, so the
compiler stops you mixing a row where a column is expected. Operators are defined
as methods:
```scala
case class CoordRow(value: Int) extends Coord:
  def +(a: Coord): CoordRow = CoordRow(value + a.value)
  def %(m: Coord): CoordRow = CoordRow((value + m.value) % m.value)
```
```kotlin
data class CoordRow(override val value: Int) : Coord() {
    override operator fun plus(other: Coord): CoordRow = CoordRow(value + other.value)
    override operator fun rem(other: Coord): CoordRow = CoordRow((value + other.value) % other.value)
}
```
**Clojure** — a coordinate is just an `Int` inside a vector; wrapping is plain `mod`,
no types, no operator overloading:
```clojure
[(mod (+ r dr) rows) (mod (+ c dc) cols)]
```

## 6. Factory / construction

| | |
|---|---|
| Java   | `Counter() { value = 0; }`  — a constructor |
| Scala  | `object Counter:`<br>&nbsp;&nbsp;`def apply(): Counter = Counter(0)` |
| Kotlin | `data class Counter(val value: Int = 0)`  — default argument |
| Clojure| `0`  — a counter is just a number from `map-indexed` |

For `Board`'s 2-arg factory, Kotlin uses `companion object { operator fun invoke(...) }`
as the analogue of Scala's companion `apply`; Java just uses an overloaded constructor.

## 7. Entry point & how you run it

| | Entry point | Build | Run in IntelliJ |
|---|---|---|---|
| Java    | `void main()` (instance main) | none / single file       | Built-in, green ▶ (Java 21+) |
| Scala   | `@main def main()`        | sbt (`build.sbt`)            | Built-in Scala plugin, green ▶ |
| Kotlin  | top-level `fun main()`    | Gradle (`build.gradle.kts`)  | Bundled Gradle, green ▶ |
| Clojure | `(defn -main [& _] ...)`  | tools.deps (`deps.edn`)      | **Cursive plugin**, REPL-driven |

Note: the Java version uses the modern **instance `main` + `IO.println`** (JEP 512 /
"implicitly declared classes," finalized in Java 25) — no `public static void
main(String[])`, no `System.out`. Worth a slide on how much boilerplate Java itself
has shed.

---

## The takeaway

- **Java → Scala/Kotlin**: the same OO design, but the big shift is
  **mutable → immutable**. Java mutates cells, the board and the counter in
  place inside a `while (true)`; Scala and Kotlin rebuild new values with `copy`
  and recurse. Java also carries the most ceremony (explicit getters, no value
  semantics, hand-written `plus`/`mod`) — though modern Java has shed a lot of it
  (`record`s, `var`, instance `main`, `IO.println`).
- **Scala ↔ Kotlin**: a near 1:1 translation. Same objects, same `copy`, same
  immutability. The diffs are cosmetic — `case class` vs `data class`,
  `match`/extractors vs `when`/guards, `@tailrec` vs `tailrec`, `def +` vs
  `operator fun plus`. Kotlin is a bit more explicit (`val`, `override`,
  fixed operator names); Scala is a bit more expressive (extractors,
  arbitrary operators).
- **→ Clojure**: a different way of thinking. Delete the types, represent the
  problem as generic immutable data, transform it with pure functions, and let
  laziness handle "forever." Roughly a quarter of the code, and the algorithm
  reads like the *definition* of the rules rather than an implementation of them
  — at the cost of the compile-time safety the typed `CoordRow`/`CoordCol`
  gave you.
