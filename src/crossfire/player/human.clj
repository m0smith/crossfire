(ns crossfire.player.human
  (:use [crossfire.player :only [opponents take-shot]]
        [crossfire.board :only [open-coods print-board]])
  ( :require [crossfire.protocol.player :as P]))

(defrecord Human [playerid boardid name status])

(defn make-human-player [playerid boardid name status]
  (Human. playerid boardid name status))

(defn choose-opponent [world player]
  (let [ops (opponents world player)
        opmap (zipmap (map :playerid ops) ops)]
    (if (> (count ops) 1)
      (do
        (print "Pick opponent:")
        (println (map :playerid ops))
        (let [rtnval (get opmap (read-string (read-line)))]
          (println opmap rtnval)
          rtnval))
      (first ops))))

(defn choose-cood [world opponent]
  (print-board world opponent)
  (print "Choose cood [x y]:")
  (let [cood (read-string (read-line))]
    cood)
)

(defn- human-make-move* [world player]
  
  (let [opponent (choose-opponent world player)
        cood (choose-cood world opponent)
        result (take-shot world player opponent cood)]
    result))

(extend-type Human
  P/Player
  (make-move [player world] (human-make-move* world player)))
