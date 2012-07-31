(ns crossfire.miss
  (:require [crossfire.protocol.location :as loc]))

(defn place-peg-in-board* [world player cood piece]
  (assoc-in world [(player :boardid) :coods cood] piece))

(defrecord Miss [cood])

(extend-type Miss 
  loc/Location
  (open? [this cood] nil)
  (place-peg-in-board [this world player cood] (place-peg-in-board* world player cood this))
  (display [this cood] :miss) 
  (place-peg [this cood] { :peg this :result :miss :cood cood}))

(extend-type nil
  loc/Location
  (open? [this cood] true)
  (display [this cood] nil)
  (place-peg-in-board [this world player cood] (place-peg-in-board* world player cood this))
  (place-peg [this cood] { :peg (Miss. cood) :result :miss :cood cood}))
