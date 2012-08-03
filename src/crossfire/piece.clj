(ns crossfire.piece
  (:require [crossfire.protocol.location :as loc])
  (:use 
        [crossfire.cood :only [cood+]]
        [crossfire.board :only [empty-cood? empty-coods get-peg-at]]))

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
    (empty-cood? world player cood)))

(defn place-piece? [world player piece]
  (let [coods (-> piece :coods-map keys)]
    (not-any? false? (map #(place-piece-cood? world player %) coods))))

(defn place-piece [world player piece]
  (let [boardid (:boardid player)
        coods (-> piece :coods-map keys)]
    (reduce #(loc/place-peg-in-board* %1 player %2 piece) world coods)))

(defn random-place-piece [world player prototype]
  (let [coods (empty-coods world player)
        pieces (map make-piece-at (repeat prototype) coods)
        valid-pieces (filter #(place-piece? world player %) pieces)]
    (if (seq valid-pieces) (place-piece world player (rand-nth valid-pieces))
        world)))

(defn is-pegged? [piece cood]
  "Return true if this is pegged nil otherwise"
  (let [coods-map (get piece :coods-map)]
    (get-in coods-map [ cood :pegged?]))
  )

(extend-type Piece
  loc/Location
  (open? [this cood] (not (is-pegged? this cood)))
  (display [this cood] (if (is-pegged? this  cood) :hit :open))
  (place-peg-in-board [this world player cood] (place-piece world player this))
  (place-peg [this cood]
    (let [new-peg (assoc-in this [ :coods-map cood :pegged?] true) ]
      {:peg new-peg :result (result new-peg) :cood cood})))
