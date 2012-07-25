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
  (-> world (get-board player) :coods (get cood)))

(defn board-contains [world player cood]
  (let [[width height] (:dim world)
        [x y] cood]
    (and (< x width) (< y height))))

(defn print-board [world player]
  (let [b (get-board world player)
        [width height] (:dim world)]
    (doseq [row  (partition width
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

(defn empty-coods [world player]
  (let [b (get-board world player)
        [width height] (:dim world)]
    (for [y (range height)
          x (range width)
          :when (not (get-in b [:coods [x y]]))]
      [x y])))

(defn available-coods [world player]
  (let [b (get-board world player)
        [width height] (:dim world)]
    (for [y (range height)
          x (range width)
          :when (available? (get-peg-at world player [x y]) world player [x y])]
      [x y])))