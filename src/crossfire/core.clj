(ns crossfire.core
  (:require [crossfire.protocol.location :as loc]
            [crossfire.protocol.player :as P]
            [crossfire.player.randomai :as ai]
            [crossfire.player.human :as human])
  (:use [crossfire.board :only [  print-final-boards  print-boards]]
        [crossfire.piece :only [random-place-piece]]
        [crossfire.player :only [ active-players update-player-status]]))

(def prototypes [{ :delta-coods [[0 0] [0 1]]}
                 { :delta-coods [[0 0] [1 0] [2 0]]}
                 { :delta-coods [[0 0] [1 0] [1 1]]}])

(def players [(human/make-human-player :p1 :c1 "C1" :active)
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


(defn game-running? [world]
  (> (count (active-players world)) 1))

(defn place-peg-in-world [world player cood piece]
  (loc/place-peg-in-board piece world player cood))

(defn take-turn
  "Taking turn has the following step:
     0 - Verify that the game is still running
     1 - Choose and opponent
     2 - Choose a cood to attack on the opponents board
     3 - Attack
     4 - Update the world with the attack
     Return the new state of the world"
  [world player]
  (if (game-running? world)
    
    (let [result (P/make-move player world)
          opponent (:opponent result)]
      (println (:name player) " attacks " (:name opponent)
               " at " ( :cood result) " with result " (:result result))
      (-> world
          (place-peg-in-world opponent (:cood result) (:peg result))
          (update-player-status opponent)
          )
      )
    world))

(defn game-seq [world]
  (lazy-seq
   (let [next (reduce take-turn world (active-players world))]
     (if (game-running? next)
       (cons next (game-seq next))
       (cons next nil)))))

(defn init-world [world]
  (reduce #(apply random-place-piece %1 %2)
          world
          (for [prototype prototypes
                player (active-players world)]
            [player prototype])))

(defn -main [& args]
  (let [gen (init-world start-world)
        game (game-seq gen)
        players (active-players gen)]
    (print-boards gen players)
    (let [winner (first (active-players (last game)))]
      (print-final-boards (last game) players winner))
    (shutdown-agents)))
