(ns crossfire.ui.tty
  (:require [crossfire.protocol.ui :as ui])
  (:use [crossfire.player :only [opponents]]
        [crossfire.board :only [open-coods print-board]]))

(defrecord TTY [])

(defn create [] (TTY.))

(defn choose-opponent [world player]
  (let [ops (opponents world player)
        opmap (zipmap (map :playerid ops) ops)]
    (if (> (count ops) 1)
      (do
        (print "Pick opponent from:")
        (println (map :playerid ops))
        (let [rtnval (get opmap (read-string (read-line)))]
          (println opmap rtnval)
          rtnval))
      (first ops))))

(defn choose-cood [world opponent]
  (print-board world opponent)
  (print "Choose cood [x y]:")
  (let [cood (read-string (read-line))]
    cood))

(defn- make-move* [world player]
  (let [opponent (choose-opponent world player)
        cood (choose-cood world opponent)]
    [opponent cood]))

(extend-type TTY
  ui/UI
  (init [ui world player])
  (choose-shot [ui world player] (make-move* world player)))