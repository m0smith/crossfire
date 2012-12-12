(ns crossfire.player
  (:use [crossfire.board :only [create-board empty-locations get-board open-locations]]
        [crossfire.cood :only [cood+]]
        [crossfire.world :only [with-world active-world? get-world]]
        [clojure.set :only [difference]]))

;; ----------------------------------------
;; PLAYER
;;
;; :playerid - the unique id for this player
;; :name - The display name for the player
;; :board - a board
;; :pieces - a set of vector of cood
;; :active - boolean: true if the player is still active, false once the player loses
;; 
;; ---------------------------------------



(defn all-players [worldref]
  (with-world [w worldref]
    (map second (:players w))))

(defn active-players [worldref]
  (filter :active (all-players worldref)))

(defn opponents-of [worldref playerid]
  (filter #(not= playerid (:playerid %)) (all-players worldref)))

(defn active-opponents-of [worldref playerid]
  (filter :active ( opponents-of worldref playerid)))

(defn- new-player [playerid name]
  {:playerid playerid
   :name name
   :board (create-board)
   :pieces #{}
   :active true
  })

  
(defn add-player! "Add a player to the world"
  [worldref playerid name watch]
  (add-watch worldref playerid watch)
  (swap! worldref #(-> %
                       (assoc-in [:players playerid] (new-player playerid name))
                       (update-in [:turn] conj playerid)
                       (update-in [:seqid] inc))))


(defn get-player [worldref playerid]
  (with-world [world worldref]
    (get-in world [:players playerid]))
  )

(defn make-piece-at "A template is a seq of coods. Return a seq of coods with cood applied to each element
of template"
  [template cood]
  (map (partial cood+ cood) template))

(defn valid-piece? "Return the piece if this piece can be placed on this board"
  [board piece]
  (let [empty-locs (set (empty-locations board))
        rtnval     (not (seq (difference (set piece) empty-locs)))]
    (when rtnval piece)))

(defn place-piece! "Place a piece based in a peice template into the board for the given player.
The piece is computed by adding start to each element of template.
Return nil if the piece cannot be placed or the computed peice"
  [worldref playerid template start]
  (if-let [player (get-player worldref playerid)]
    (let [piece (make-piece-at template start)
          board (get-board player)]
      (if (valid-piece? board piece)
        (do
          (println "placing piece " piece)
          (swap! worldref (fn [w]
                         (-> (reduce #(update-in %1 [:players playerid :board :coods] assoc %2 :open) w piece)
                             (update-in [:players playerid :pieces] conj piece)
                             (update-in [:seqid] inc))))
          piece)
        
        ))))

(defn player-active? [world playerid]
  (let [player (get-player world playerid)]
    (println "====== player-active?" player)
    (:active player)))

(defn player-turn? [world playerid]
  (= playerid (first (:turn world))))

(defn shoot-cood [coods cood]
  (if (= :open (get coods cood))
    :hit
    :miss))

(defn next-turn [world]
  (if (active-world? world)
    (let [turn-vec (:turn world)
          sz (count turn-vec)]
      (loop [turns (rest (cycle turn-vec))]
        (println "====== next-turn " (first turns) (player-active? world (first turns)) world)
        (if (player-active? world (first turns))
          (assoc world :turn (vec (take sz turns)))
          (recur (rest turns)))))
    world))


(defn player-has-ships? [worldref playerid]
  (with-world [world worldref]
    (when (not (seq (open-locations (get-board world playerid))))
      (assoc-in world [:players playerid :active] false))))

(defn update-game-status [world]
  (let [current-status (:status world)]
    (when (= current-status :active)
      (let [ status? (> (count (active-players world)) 1)
            status (if status? current-status :over)]
        (if (= :over status) (println "====== GAME OVER ===="))
        (assoc world :status status)))))
(defn echo [msg id]
 ;; (println "echo " id msg)
  msg)

(defn make-move-internal [world opponentid cood]
  (println "make-move-internal start")
  (let [rtnval (-> world
                   (update-in [:seqid] inc)
                   ( echo "1")
                   (update-in [:players opponentid :board :coods] shoot-cood cood)
                   ( echo "2")
                   (player-has-ships? opponentid)
                   ( echo "3")
                   update-game-status
                   ( echo "4")
                   next-turn
                   (echo "5"))]
    (println "make-move-internal:" rtnval)
    rtnval))

(defn make-move! [world playerid opponentid cood]
  (when (and (active-world? world)
             (player-turn? world playerid))
    (let [worldref (get-world (:worldid world))]
      (println "make-move! " (:worldid world) playerid opponentid cood)
      (swap! worldref make-move-internal opponentid cood)
      (println "make-move! POST:" @worldref))
    ))

(defn debug-watch [ key _ ___ new]
  (println "DEBUG====" key new))

  ;; (defn match-in? [val kys]
  ;;   (fn [m] (when (#{val} (get-in m kys))
  ;;             m)))

  ;; (defn get-player [world playerid]
  ;;    (some (match-in? playerid [:playerid]) (all-players world)))

  ;; (defn active-player? [player]
  ;;   ( #{:active} (:status player)))

  ;; (defn active-players [world]
  ;;   (filter active-player? (all-players world)))


  ;; (defn opponent? [player other-player]
  ;;   (not= player other-player))

  ;; (defn opponents [world player]
  ;;   (filter (partial opponent? player) (active-players world)))

  ;; (defn player-status [world player]
  ;;   (let [board (get-board world player)
  ;;         coods (open-coods world player)
  ;;         stats (map #(loc/display (get-peg-at world player %) %) coods)]
  ;;     (some #(= % :open) stats)))


  ;; (defn compute-updated-players [players playerid status]
  ;;   (for [player players]
  ;;     (if (= playerid (:playerid player))
  ;;       (assoc-in player [:status] status)
  ;;       player))
  ;;   )


  ;; (defn update-player-status [world player]
  ;;   (let [stat (player-status world player)]
  ;;     (if stat world
  ;;         (do
  ;;           (println (:name player) " has been defeated")
  ;;           (update-in world [:players] compute-updated-players (:playerid player) :defeated)))))

  ;; (defn take-shot [world player opponent cood]
  ;;   (merge {:opponent opponent
  ;;           :worldid (:worldid world)
  ;;           :playerid (:playerid player)
  ;;           :opponentid (:playerid opponent)}
  ;;          (loc/place-peg (get-peg-at world opponent cood) cood)) )


;;)