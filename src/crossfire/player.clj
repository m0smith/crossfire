(ns crossfire.player
  (:use [crossfire.board :only [create-board empty-locations get-board]]
        [crossfire.cood :only [cood+]]
        [clojure.set :only [difference]]))

;; ----------------------------------------
;; PLAYER
;;
;; :playerid - the unique id for this player
;; :name - The display name for the player
;; :board - a board
;; :pieces - a set of vector of cood
;; 
;; ---------------------------------------

(defn all-players [world]
  (:players @world))

(defn- new-player [playerid name]
  {:playerid playerid
   :name name
   :board (create-board)
   :pieces #{}
  })

  
(defn add-player! "Add a player to the world"
  [worldref playerid name watch]
  (add-watch worldref playerid watch)
  (swap! worldref #(-> %
                    (assoc-in [:players playerid] (new-player playerid name))
                    (update-in [:seqid] inc))))


(defn get-player* [world playerid]
  (get-in world [:players playerid]))

(defn get-player [worldref playerid]
  (get-player* @worldref playerid)
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
                         (-> (reduce #(update-in %1 [:players playerid :board :coods] assoc %2 :open ) w piece)
                             (update-in [:players playerid :pieces] conj piece)
                             (update-in [:seqid] inc))))
          piece)
        
        ))))

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