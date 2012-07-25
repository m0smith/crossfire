(ns crossfire.miss
  (use [crossfire.protocol.location]
       [crossfire.board :only [board-contains]]))


(defn place-peg-in-board* [world player cood piece]
  (assoc-in world [(player :boardid) :coods cood] piece))

(defrecord Miss [cood])

(extend-type Miss 
  Location
  (is-pegged? [this world player cood] this)
  (available? [this world player cood] nil)
  (place-peg-in-board [this world player cood] (place-peg-in-board* world player cood this))
  (display [this cood] :miss) 
  (place-peg [this cood] { :peg this :result :miss :cood cood}))

(extend-type nil
  Location
  (is-pegged? [t w p _] nil)
  (available? [this world player cood] (board-contains world player cood))
  (display [this cood] nil)
  (place-peg-in-board [this world player cood] (place-peg-in-board* world player cood (Miss. cood)))
  (place-peg [this cood] { :peg (Miss. cood) :result :miss :cood cood}))