(ns crossfire.board
  (:require [crossfire.miss]
            [crossfire.protocol.location :as loc]))

(def display-map {nil "."
                  :miss "!"
                  :hit "X"
                  :open "O"})

(defn get-board [world player]
  (get-in world [:boards (:boardid player)]))

(defn get-dimensions
  ([board] (:dim board))
  ([world player] (get-dimensions (get-board world player))))

(defn get-peg-at [world player cood]
  (get-in (get-board world player) [:coods cood]))

(defn board-contains? [world player cood]
  (let [[width height] (get-dimensions world player)
        [x y] cood]
    (and (< x width) (< y height))))

(defn print-board [world player]
  (let [[width height] (get-dimensions world player)]
    (doseq [row (partition width
                           (for [y (range height) x (range width)]
                             (let [p (get-peg-at world player [x y])]
                               (display-map (loc/display p [x y]))
                               )))]
      (println row))))

(defn print-boards [world players]
  (doseq [p players]
    (println (:name p))
    (print-board world p)
    (println "---------")))

(defn print-final-boards [world players winner]
  (let []
    (println "============= GAME OVER =========" )
    (print-boards world players)
    (println "WIN" (:name winner))))

(defn empty-cood? [world player cood]
  "Return true if a peice or a peg can be placed here"
  (let [board (get-board world player)]
    (and (board-contains? world player cood)
         (not (get-in board [:coods cood])))))

(defn open-cood? [world player cood]
  "Return true if a peg can be placed at cood.  Return nil if cood is
not in the board or if it already has a peg."
  (and (board-contains? world player cood)
       (loc/open? (get-peg-at world player cood) cood)))

(defn matching-coods [pred world player]
  "Return a lazy-seq of the all the coods for which pred returns
true. Pred is passed world, player and cood"
  (let [[width height]  (get-dimensions world player)]
    (for [y (range height)
          x (range width)
          :when (pred world player [x y])]
      [x y])))

(defn open-coods [world player]
  (matching-coods open-cood? world player))

(defn empty-coods [world player]
  (matching-coods empty-cood? world player))
