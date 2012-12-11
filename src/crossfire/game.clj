(ns crossfire.game
  (:use [crossfire.world :only [create-world get-world]]))



;; (defn game-running? [world]
;;   (> (count (active-players world)) 1))

;; (defn place-peg-in-world [world player cood piece]
;;   (loc/place-peg-in-board piece world player cood))

;; (defn merge-into-worlds [world worldid]
;;   (swap! worlds assoc worldid world)
;;   world)

;; (defn current-player [world]
;;   (first (:player-seq world)))

;; (defn select-next-player [world]
;;   (update-in world [:player-seq] rest)
;;   world)

;; (defn skipped-move [{:keys [worldid]}]
;;   (-> (get-world worldid)
;;       (select-next-player)
;;       (merge-into-worlds worldid)
;;       ))

;; (defn made-move [{:keys [worldid playerid opponentid cood result]}]
;;   (let [world (get-world worldid)
;;         player (get-player world playerid)
;;         opponent (get-player world opponentid)]
;;     (println (:name player) " attacks " (:name opponent)
;;              " at " cood " with result " (:result result))
;;     (-> world
;;         (place-peg-in-world opponent (:cood result) (:peg result))
;;         (update-player-status opponent)
;;         (select-next-player)
;;         (merge-into-worlds worldid)
;;         )))

;; (defn move-callback [{:keys [opponentid]  :as m}]
;;   (take-turn (if opponentid 
;;       (made-move m)
;;       (skipped-move m))))

;; (defn take-turn
;;   "Taking turn has the following step:
;;      0 - Verify that the game is still running
;;      1 - Choose and opponent
;;      2 - Choose a cood to attack on the opponents board
;;      3 - Attack
;;      4 - Update the world with the attack
;;      Return the new state of the world"
;;   [world]
;;   (let [player (current-player world)]
;;     (if (game-running? world)
;;       (future (P/make-move player world move-callback))
;;       world)))

;; (defn active-players-seq [worldid]
;;   (repeatedly #(filter active-player? (active-players (get-world worldid)))))

;; (defn game-seq-new [worlds worldid]
;;   (lazy-seq
;;    (let [world (get-world worldid)
;;          next (reduce take-turn world (active-players world))]
;;      (if (game-running? next)
;;        (cons next (game-seq-new next))
;;        (cons next nil)))))

;; (defn game-seq [world]
;;   (lazy-seq
;;    (let [next (reduce take-turn world (active-players world))]
;;      (if (game-running? next)
;;        (cons next (game-seq next))
;;        (cons next nil)))))

;; (defn place-pieces [world]
;;   (reduce #(apply random-place-piece %1 %2)
;;           world
;;           (for [prototype prototypes
;;                 player (active-players world)]
;;             [player prototype])))




