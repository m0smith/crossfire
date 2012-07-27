(ns crossfire.board
  (use [crossfire.protocol.location]))

(def display-map {
                  nil "."
                  :miss "!"
                  :hit "X"
                  :open "O"})

(defn get-board [world player]
  (world (player :boardid)))

(defn get-peg-at [world player cood]
  (get-in (get-board world player) [:coods cood]))

(defn board-contains? [world player cood]
  (let [[width height] (:dim world)
        [x y] cood]
    (and (< x width) (< y height))))

(defn print-board [world player]
  (let [b (get-board world player)
        [width height] (:dim world)]
    (doseq [row (partition width
                           (for [y (range height) x (range width)]
                             (let [cood [x y]
                                   p (get-in b [:coods cood])
                                   dis (display p cood)]
                               (display-map dis))))]
      (println row))))

(defn print-boards [world players]
  (doseq [p players]
    (println (:name p))
    (print-board world p)
    (println "---------")))

(defn print-final-boards [world players winner]
  (println "============= GAME OVER =========" )
  (print-boards world players)
  (println "WIN" winner)
  )

(defn empty-cood? [world player cood]
  "Return true if a peice or a peg can be placed here"
  (let [board (get-board world player)]
    (and (board-contains? world player cood)
         (not (get-in board [:coods cood])))))

(defn empty-coods [world player]
  "Return a lazy seq of all cood that have neither peg nor peice"
  (let [b (get-board world player)
        [width height] (:dim world)]
    (for [y (range height)
          x (range width)
          :when (empty-cood? world player [x y])]
      [x y])))

(defn open-cood? [world board cood]
  "Return true if a peg can be placed at cood.  Return nil if cood is not in the board or if it already has a peg."
  (and (board-contains? world board cood)
       (open? (get-peg-at world board cood) cood)))

(defn open-coods [world player]
  "Return a lazy-seq of the all the coods that can receive a peg"
  (let [b (get-board world player)
        [width height] (:dim world)]
    (for [y (range height)
          x (range width)
          :when (open-cood? world player [x y])]
      [x y])))