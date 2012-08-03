(ns crossfire.protocol.location)

(defprotocol Location
  (open? [this cood]
    "Return true if a peg can be placed at this location.  Return nil if the cood has a peg .")
  (display [this cood])
  (place-peg-in-board [this world player cood])
  (place-peg [this cood] "Return the state as a map
     { :peg <Location impl>
       :cood
       :result one of :miss :hit or :sunk
     }"))

(defn place-peg-in-board* [world player cood piece]
  (assoc-in world [:boards (:boardid player) :coods cood] piece))
