(ns crossfire.miss
  (use [crossfire.protocol.peg]
       [crossfire.board :only [board-contains]]))

(defrecord Miss [cood])

(extend-type Miss 
  Peg
  (is-pegged? [this world player cood] this)
  (available? [this world player cood] nil)
  (display [this cood] :miss) 
  (peg [this cood] { :peg this :result :miss :cood cood}))

(extend-type nil
  Peg
  (is-pegged? [t w p _] nil)
  (available? [this world player cood] (board-contains world player cood))
  (display [this cood] nil)
  (peg [this cood] { :peg (Miss. cood) :result :miss :cood cood}))