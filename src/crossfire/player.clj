(ns crossfire.player
  (use [crossfire.board :only [get-board get-peg-at available-coods]]
       [crossfire.protocol.location]))

(defn all-players [world]
  (let [players (:players world)]
    players))

(defn active-players [world]
  (filter #( #{:active} (:status %)) (all-players world)))


(defn opponent? [player other-player]
  (not= player other-player))

(defn opponents [world player]
  (filter (partial opponent? player) (active-players world)))

(defn player-status [world player]
  (let [board (get-board world player)
        coods (available-coods world player)
        stats (map #(display (get-peg-at world player %) %) coods)]
    (some #(= % :open) stats)))


(defn compute-updated-players [players playerid status]
  (for [player players]
    (if (= playerid (:playerid player))
      (assoc-in player [:status] status)
      player))
  )


(defn update-player-status [world player]
  (let [stat (player-status world player)]
    (if stat world
        (do
          (println (:name player) " has been defeated")
          (update-in world [:players] compute-updated-players (:playerid player) :defeated)))))
