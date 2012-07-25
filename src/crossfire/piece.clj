(ns crossfire.piece
  (use [crossfire.protocol.location]
       [crossfire.cood :only [cood+]]
       [crossfire.board :only [available-coods get-peg-at]]))

(defrecord Piece [coods-map])

(defn result [piece]
  (if (not-any? nil? (map :pegged? (vals (:coods-map piece))))
    :sunk :hit))


(defn make-piece-at [prototype cood]
  (let [coods (map cood+ (repeat cood) (:delta-coods prototype))
        coods-map (reduce #( merge %1 {%2 nil}) {} coods )]
    (Piece. coods-map)))

(defn place-piece-cood? [world player cood]
  (let [ peg (get-peg-at world player cood)]
    (available? peg world player cood)))

(defn place-piece? [world player piece]
  (let [coods (-> piece :coods-map keys)]
    (not-any? false? (map #(place-piece-cood? world player %) coods))))

(defn place-piece [world player piece]
  (let [boardid (:boardid player)
        coods (-> piece :coods-map keys)]
    (reduce #(assoc-in %1 [boardid :coods %2] piece) world coods)))

(defn random-place-piece [world player prototype]
  (let [coods (available-coods world player)
        pieces (map make-piece-at (repeat prototype) coods)
        valid-pieces (filter #(place-piece? world player %) pieces)]
    (if (seq valid-pieces) (place-piece world player (rand-nth valid-pieces))
        world)))

(extend-type Piece
  Location
  (is-pegged? [this world player cood] (get-in this [:coods-map cood :pegged?]))
  (available? [this world player cood] (not (is-pegged? this world player cood)))
  (display [this cood] (if (is-pegged? this nil nil cood) :hit :open))
  (place-peg-in-board [this world player cood] (place-piece world player this))
  (place-peg [this cood]
    (let [new-peg (assoc-in this [ :coods-map cood :pegged?] true) ]
      {:peg new-peg :result (result new-peg) :cood cood})))
