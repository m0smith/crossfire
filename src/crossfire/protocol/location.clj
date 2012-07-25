(ns crossfire.protocol.location)

(defprotocol Location
  (is-pegged? [this world player cood])
  (available? [this world player cood])
  (display [this cood])
  (place-peg-in-board [this world player cood])
  (place-peg [this cood] "Return the state as a map
     { :peg <Location impl>
       :cood
       :result one of :miss :hit or :sunk
     }"))
