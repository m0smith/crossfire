(ns crossfire.board
  (:use [crossfire.world :only [with-world]]))

;;----------------------------------------
;; BOARD
;; :coods - map of cood to one of :miss :hit :open :empty
;;          A shipped placed at that cood is considered :open if not hit
;;          nil is the same as :empty
;; :dim  the size of the board as [width height]
;;----------------------------------------

(def display-dictionary
  {nil "."
   :empty "."
   :miss "!"
   :hit "X"
   :open "O"})

(def opponent-dictionary
  {nil :empty
   :miss :miss
   :hit :hit
   :open :empty})

(def player-dictionary
  {nil :empty
   :miss :miss
   :hit :hit
   :open :open})

(defn get-board
  ([worldref playerid]
     (with-world [world worldref]
       (get-in world [:players playerid :board])))
  ( [player]
      (:board player)))

(defn create-board
  ([] (create-board [10 10]))
  ([dim]
     {
      :coods {}
      :dim [10 10]
      }))

(defn get-dimensions
  ([board] (:dim board))
  ([worldref playerid] (get-dimensions (get-board worldref playerid))))

;; (defn get-peg-at [world player cood]
;;   (get-in (get-board world player) [:coods cood]))

;; (defn board-contains? [world player cood]
;;   (let [[width height] (get-dimensions world player)
;;         [x y] cood]
;;     (and (< x width) (< y height))))


(defn all-location-cvals "Return a seq of cvals for all locations in this board.  A cval is a vector with
first : cood
second : value"
  ([board] (all-location-cvals board player-dictionary))
  ([board dictionary]
     (let [[width height] (get-dimensions board)
           coods (:coods board)]
       (for [y (range height) x (range width)]
         [[x y] (dictionary (coods [x y]))] ))))
            
(defn filted-location-cvals "Return a seq of cvals where the value is function/pred that will
accept a value from the cood and return truethy if it is to be included in the results. A set with
the desired values will just do the trick"
  ([board value] (filted-location-cvals board value player-dictionary))
  ([board value dictionary]
     (filter #(value (second %)) (all-location-cvals board dictionary))))

(defn empty-locations
  ([board] (empty-locations board player-dictionary))
  ([board dictionary] (map first (filted-location-cvals board #{:empty} dictionary))))

(defn open-locations
  ([board] (open-locations board player-dictionary))
  ([board dictionary] (map first (filted-location-cvals board #{:open} dictionary))))
              
;; (defn player-board-locations [world player]
;;   (all-board-locations world player player-dictionary))

;; (defn opponent-board-locations [world opponent]
;;   (all-board-locations world opponent opponent-dictionary))


;; (defn print-board [world player]
;;   (let [[width height] (get-dimensions world player)]
;;     (doseq [row (partition width (all-board-locations world player display-dictionary))]
;;       (println (map second row)))))

;; (defn print-boards [world players]
;;   (doseq [p players]
;;     (println (:name p))
;;     (print-board world p)
;;     (println "---------")))

;; (defn print-final-boards [world players winner]
;;   (let []
;;     (println "============= GAME OVER =========" )
;;     (print-boards world players)
;;     (println "WIN" (:name winner))))

;; (defn empty-cood? [world player cood]
;;   "Return true if a peice or a peg can be placed here"
;;   (let [board (get-board world player)]
;;     (and (board-contains? world player cood)
;;          (not (get-in board [:coods cood])))))

;; (defn open-cood? [world player cood]
;;   "Return true if a peg can be placed at cood.  Return nil if cood is
;; not in the board or if it already has a peg."
;;   (and (board-contains? world player cood)
;;        (loc/open? (get-peg-at world player cood) cood)))

;; (defn matching-coods [pred world player]
;;   "Return a lazy-seq of the all the coods for which pred returns
;; true. Pred is passed world, player and cood"
;;   (let [[width height]  (get-dimensions world player)]
;;     (for [y (range height)
;;           x (range width)
;;           :when (pred world player [x y])]
;;       [x y])))

;; (defn open-coods [world player]
;;   (matching-coods open-cood? world player))

;; (defn empty-coods [world player]
;;   (matching-coods empty-cood? world player))
