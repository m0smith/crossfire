(ns crossfire.core
  (use [crossfire.board :only [available-coods get-peg-at print-final-boards]]
       [crossfire.protocol.location]
       [crossfire.miss]
       [crossfire.piece :only [random-place-piece]]
       [crossfire.player :only [opponents active-players update-player-status]]))

(def prototypes [{ :delta-coods [[0 0] [0 1]]}
                 { :delta-coods [[0 0] [1 0] [2 0]]}
                 { :delta-coods [[0 0] [1 0] [1 1]]}])

(def players [{:playerid :p1 :boardid :c1 :name "C1" :status :active}
              {:playerid :p2 :boardid :c2 :name "C2" :status :active}
              {:playerid :p3 :boardid :c3 :name "C3" :status :active}
              {:playerid :p4 :boardid :c4 :name "C4" :status :paused} ])

(def start-world {:dim [10 4]
                  :players players})

(defn take-shot [world player opponent cood]
  (place-peg (get-peg-at world player cood) cood))


(defn game-running? [world]
  (> (count (active-players world)) 1))

(defn place-peg-in-world [world player cood piece]
  (place-peg-in-board piece world player cood))

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
    (let [opponent (rand-nth (opponents world player))
          cood (rand-nth (available-coods world opponent))
          result (take-shot world player opponent cood)]
      (println (:name player) " attacks " (:name opponent)
               " at " cood " with result " (:result  result))
      (-> world
          (place-peg-in-world opponent cood (result :peg))
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



(defn run-game [world]
  (let [game-players (active-players world)]
    (for [w (take 100 (game-seq world))]
      (if (not (game-running? w))
        (do
          (print-final-boards w game-players (map :name (active-players w)))
          w)))))


(defn init-world [world]
  (reduce #(apply (partial random-place-piece %1) %2)
          world
          (for [prototype prototypes
                player (active-players world)]
            [player prototype])))

(defn -main [& args]
  (doall (run-game (init-world start-world)))
  
  )