(ns crossfire.core
  (:require  [crossfire.player.randomai :as ai]
             [crossfire.player.human :as human]
             [crossfire.ui.tty :as tty])
  (:use [crossfire.board  :only [print-final-boards  print-boards]]
        [crossfire.game   :only [init-world game-seq]]
        [crossfire.player :only [active-players]]
        ))

(def players [(human/make-human-player :p1 :c1 "C1" :active (tty/create))
              (ai/make-random-ai :p2 :c2 "C2" :active)
              (ai/make-random-ai :p3 :c3 "C3" :active)
              (ai/make-random-ai :p4 :c4 "C4" :paused)
              ])

(def start-world {
                  :players players
                  :boards
                  { :c1 { :dim [6 8]}
                    :c2 { :dim [8 6]}
                    :c3 { :dim [12 4]}
                   }})


(defn -main [& args]
  (let [gen (init-world start-world)
        game (game-seq gen)
        players (active-players gen)]
    (print-boards gen players)
    (let [winner (first (active-players (last game)))]
      (print-final-boards (last game) players winner))
    ))
