(ns crossfire.player.human
  (:require [crossfire.protocol.player :as P]
            [crossfire.protocol.ui :as ui])
  (:use [crossfire.player :only [take-shot]]))

(defrecord Human [playerid boardid name status ui])

(defn make-human-player [playerid boardid name status ui]
  (->Human playerid boardid name status ui))

(extend-type Human
  P/Player
  (make-move [player world callback]
    (let [[opponent cood] (ui/choose-shot (:ui player) world player)]
      (take-shot world player opponent cood))))
