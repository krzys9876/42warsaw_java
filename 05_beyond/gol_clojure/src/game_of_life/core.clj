(ns game-of-life.core
  (:require [clojure.string :as str]))

;; The whole world is just an immutable set of live cells, where each
;; cell is a [row col] pair. No Board, no Cell, no Coord classes.

(def rows 15)
(def cols 25)

(defn neighbours
  "The 8 cells around a cell, wrapping around the edges (toroidal board)."
  [[r c]]
  (for [dr [-1 0 1]
        dc [-1 0 1]
        :when (not (and (zero? dr) (zero? dc)))]
    [(mod (+ r dr) rows)
     (mod (+ c dc) cols)]))

(defn step
  "One generation. Count how many times each cell is a neighbour of a
   live cell; a cell lives if it has 3 neighbours, or 2 and was alive."
  [live]
  (set
    (for [[cell n] (frequencies (mapcat neighbours live))
          :when (or (= n 3)
                    (and (= n 2) (live cell)))]
      cell)))

(defn glider
  "A glider as a set of live cells, anchored at [r c]."
  [r c]
  #{[r (+ c 1)]
    [(+ r 1) (+ c 2)]
    [(+ r 2) c]
    [(+ r 2) (+ c 1)]
    [(+ r 2) (+ c 2)]})

(def initial
  (into (glider 5 2) (glider 8 14)))

(defn render [live]
  (str/join "\n"
    (for [r (range rows)]
      (str/join
        (for [c (range cols)]
          (if (live [r c]) "X " ". "))))))

(defn -main [& _]
  ;; (iterate step initial) is the infinite, lazy sequence of every
  ;; generation -- this single line replaces the recursive run() loop.
  (doseq [[gen live] (map-indexed vector (iterate step initial))]
    (print "\u001b[H\u001b[2J")
    (println (render live))
    (println gen)
    (flush)
    (Thread/sleep 250)))
