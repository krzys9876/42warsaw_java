# Game of Life: Java · Scala · Kotlin · Clojure

|                | Java                  | Scala 3              | Kotlin                | Clojure                  |
|----------------|-----------------------|----------------------|-----------------------|--------------------------|
| **Paradigm**   | mutable OO            | immutable OO/FP      | immutable OO/FP       | data + pure functions    |
| **State**      | `Cell`/`Board` objects| same (`case class`)  | same (`data class`)   | a `set` of `[row col]`   |
| **Change**     | mutate in place       | `copy(...)`          | `copy(...)`           | new persistent value     |
| **Rules**      | `if / else if`        | `match` + extractors | `when` + guards       | `for ... :when`          |
| **`+` / `%`**  | `plus` / `mod` methods| `+` / `%` methods    | `operator fun`        | plain `+` / `mod`         |
| **Loop**       | `while (true)`        | `@tailrec`           | `tailrec`             | `(iterate step init)`    |
| **Typing**     | static                | static               | static                | dynamic                  |
| **Entry**      | `void main()`         | `@main def main`     | `fun main()`          | `(defn -main ...)`       |

### The rule, side by side

```java   // Java — mutate the cell
if (isSet()) { if (n<2) reset(); else if (n==2||n==3) set(); else if (n>3) reset(); }
else        { if (n==3) set(); }
```
```scala  // Scala — pattern match
(value, neighbours) match
  case (true,  Neighbours(n)) if n == 2 || n == 3 => set
  case (false, Neighbours(n)) if n == 3           => set
  case _                                           => reset
```
```clojure ;; Clojure — the whole board at once
(for [[cell n] (frequencies (mapcat neighbours live))
      :when (or (= n 3) (and (= n 2) (live cell)))] cell)
```

**Java → Scala/Kotlin:** same OO design, mutable → immutable.
**Scala ↔ Kotlin:** cosmetic syntax differences.
**→ Clojure:** drop the types; data + functions + laziness, ~¼ the code.
