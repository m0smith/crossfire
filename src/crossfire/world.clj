(ns crossfire.world
  (:use         [crossfire.util :only [threadid]]))

;; ========================================
;; WORLD
;; The world map has the following keys
;;  :worldid - The id used to reference this world in the worlds
;;  :seqid - A sequence number that is incremented each time ths stat of
;;            this world is altered
;;  :players - a map of playerid to players
;;  :status one of: :init :active :over
;;  :move-result - a map with
;;      :playerid - who made the move
;;      :opponentid - who was attacked
;;      :cood cood - where the move was made
;;      :result  - either :hit or :miss
;;      :piece-state - either :miss :hit or :sunk
;;      :opponent-state - :active or :over
;;  :turn - The vector of playerids in the order of thpeir turns.  The head player
;;          is the one with the current turn.  Only players with :active set to true
;;          can be the head player
;;========================================

(def worlds "A map from a worldid to a world"
  (atom {}))

(defn- base-world "Defines what the most primitive world looks like.
  It is sufficient to both validate and also to add players and
  pieces"
  [worldid]
  {:worldid worldid
   :seqid 0
   :status :init
   :message "Initializing ..."
   :players {}
   :turn []})

(defn create-world "And let there be light!
Creates a prototypical world and returns the worldid"
  [] (let [worldid (keyword (gensym "world-"))]
       (swap! worlds assoc worldid (atom (base-world worldid)))
       worldid))

(defn get-world "Returns the atom that holds the world map or nil if it
does not exist"
  [worldid] (get @worlds worldid))

(defn start-world! [worldref]
  (when (= :init (:status @worldref))
    (swap! worldref #(-> %
                         (assoc :status :active)
                         (update-in [:seqid] inc))))
  (println "end of start-world! " (threadid)))

(defn active-world? [world]
  (= (world :status) :active))

(defmacro with-world
  [bindings & body]
  `(let [~(bindings 0) (if (instance? clojure.lang.Atom ~(second bindings))
                         (deref ~(second bindings))
                         ~(second bindings))]
     ~@body))

