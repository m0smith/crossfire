(ns crossfire.miss
  (:use [crossfire.protocol.location]))

(defn place-peg-in-board* [world player cood piece]
  (assoc-in world [(player :boardid) :coods cood] piece))

(defrecord Miss [cood])

(extend-type Miss 
  Location
  (open? [this cood] nil)
  (place-peg-in-board [this world player cood] (place-peg-in-board* world player cood this))
  (display [this cood] :miss) 
  (place-peg [this cood] { :peg this :result :miss :cood cood}))

(extend-type nil
  Location
  (open? [this cood] true)
  (display [this cood] nil)
  (place-peg-in-board [this world player cood] (place-peg-in-board* world player cood this))
  (place-peg [this cood] { :peg (Miss. cood) :result :miss :cood cood}))
