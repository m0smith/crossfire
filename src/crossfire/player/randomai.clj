(ns crossfire.player.randomai
  (:use [crossfire.player :only [opponents take-shot]]
        [crossfire.board :only [open-coods]])
  ( :require [crossfire.protocol.player :as P]))

(defrecord RandomAI [playerid boardid name status ui])

(defn- make-move* [world player]
  (let [opponent (rand-nth (opponents world player))
        cood (rand-nth (open-coods world opponent))
        result (take-shot world player opponent cood)]
    result))

(extend-type RandomAI
  P/Player
  (make-move [player world callback] (callback (make-move* world player)) ))
