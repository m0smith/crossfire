(ns crossfire.player
  (:require [ crossfire.protocol.location :as loc])
  (:use [crossfire.board :only [get-board get-peg-at open-coods]]
        ))



(defn all-players [world]
  (let [players (:players world)]
    players))

(defn match-in? [val kys]
  (fn [m] (when (#{val} (get-in m kys))
            m)))
(defn get-player [world playerid]
   (some (match-in? playerid [:playerid]) (all-players world)))

(defn active-player? [player]
  ( #{:active} (:status player)))

(defn active-players [world]
  (filter active-player? (all-players world)))


(defn opponent? [player other-player]
  (not= player other-player))

(defn opponents [world player]
  (filter (partial opponent? player) (active-players world)))

(defn player-status [world player]
  (let [board (get-board world player)
        coods (open-coods world player)
        stats (map #(loc/display (get-peg-at world player %) %) coods)]
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

(defn take-shot [world player opponent cood]
  (merge {:opponent opponent
          :worldid (:worldid world)
          :playerid (:playerid player)
          :opponentid (:playerid opponent)}
         (loc/place-peg (get-peg-at world opponent cood) cood)) )


