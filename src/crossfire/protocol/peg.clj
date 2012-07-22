(ns crossfire.protocol.peg)

(defprotocol Peg
  (is-pegged? [this world player cood])
  (available? [this world player cood])
  (display [this cood])
  (peg [this cood] "Return the state as a map
     { :peg <Peg impl>
       :cood
       :result one of :miss :hit or :sunk
     }"))
