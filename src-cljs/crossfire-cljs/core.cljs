(ns crossfire-cljs.core
  (:require  [crossfire.player.randomai :as ai])
  (:use [crossfire.board :only [print-final-boards  print-boards]]
        [crossfire.game :only [init-world  game-seq]]
        [crossfire.player :only [active-players]]))

(def players [(ai/make-random-ai :p1 :c1 "C1" :active)
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



(js/alert "Starting Crossfire!")
(.write js/document "<pre>")
(binding [*print-fn*  #(.write js/document %)]
  (let [gen (init-world start-world)
        game (game-seq gen)
        players (active-players gen)]
    (print-boards gen players)
    (let [winner (first (active-players (last game)))]
      (print-final-boards (last game) players winner))
    ))