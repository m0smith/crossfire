(ns crossfire.ui.seesaw
  (:use [seesaw core graphics]
        [crossfire.player.randomai :only [ add-randomai-player!]]
        [crossfire.world :only [create-world get-world start-world!]]
        [crossfire.board :only [all-location-cvals display-dictionary player-dictionary
                                opponent-dictionary get-dimensions
                                get-board]]
        [crossfire.player :only [get-player]]))

(def prototypes [ [[0 0] [0 1]]
                  [[0 0] [1 0] [2 0]]
                  [[0 0] [1 0] [1 1]]])


;; (defn choose-opponent [world player]
;;   (let [ops (opponents world player)
;;         opmap (zipmap (map :playerid ops) ops)]
;;     (if (> (count ops) 1)
;;       (do
;;         (print "Pick opponent from:")
;;         (println (map :playerid ops))
;;         (let [rtnval (get opmap (read-string (read-line)))]
;;           (println opmap rtnval)
;;           rtnval))
;;       (first ops))))

;; (defn choose-cood [world opponent]
;;   (print-board world opponent)
;;   (print "Choose cood [x y]:")
;;   (let [cood (read-string (read-line))]
;;     cood))

;; (defn- make-move* [world player]
;;   (let [opponent (choose-opponent world player)
;;         cood (choose-cood world opponent)]
;;     [opponent cood]))

(def tile-size 5)

(defn peg-widget [[x y :as cood] tile player dictionary]
   (button
    :id tile
    ;;:border (str cood)
    :class tile
    :user-data {:cood cood
                :player player
                }
    :location [(* tile-size x) (* tile-size y)]
    :bounds [(* tile-size x) (* tile-size y) tile-size tile-size]
    :text (dictionary tile)))

;; (defn player-board-tiles [world player]
;;   (for [[cood tile] (player-board-locations world player)]
;;     (peg-widget cood tile player nil)))

(defn board-tiles* [world player dictionary]
  (let [cvals (all-location-cvals (get-board player) dictionary)]
    (for [[cood value] cvals]
      (do
        (peg-widget cood value player display-dictionary)))))

;; (defn opponent-panels [world player]
 ;;  (tabbed-panel
 ;;   :id :tabs
 ;;   :placement :left
 ;;   :tabs (for [opponent (opponents world player)]
 ;;           { :title (:name opponent)
 ;;            :content (grid-panel
 ;;                      :id (:boardid opponent)
 ;;                      :columns (first (get-dimensions world opponent))
 ;;                      :items (opponent-board-tiles world player opponent))
 ;;            })))

(defn create-tabs [world opponents dictionary]
  (for [opponent opponents]
    (let [items (board-tiles* world opponent dictionary)
          board (get-board opponent)]
      {:title (:name opponent)
       :content (grid-panel
                 :id (:playerid opponent)
                 :columns (first (get-dimensions board))
                 :items items)
       })))


(defn player-panels [world players dictionary]
  (tabbed-panel
   :id :tabs
   :placement :left
   :tabs (create-tabs world players dictionary)))

(defn add-behaviors [root]
  (println "add-behaviors start" )
  (config! (select root [:.open])
           :background :grey
           :listen [:mouse-pressed #(println (user-data %))])
  (config! (select root [:.empty])
           :background :blue
           :listen [:mouse-pressed #(println (user-data %))])
  (config! (select root [:.hit]) :background :red)
  (println "add-behaviors end")
  root)

(defn panels [world player]
  [ (player-panels world (map (partial get-player world)[:p2 :p3]) player-dictionary)
    (player-panels world [player] player-dictionary)])

(defn draw-frame [worldref playerid]
  
  (let [world @worldref
        player (get-player world playerid)]
    (invoke-later
     (->
      (frame :size [500 :by 500])
      (config!  :title "Crossfire",
                :content (border-panel
                          :vgap 5
                          :hgap 5
                          :north "Take Aim!"
                          :center (horizontal-panel
                                   :items (panels world player)
                                   )),
                :on-close :exit)
      add-behaviors
      pack!
      show!)
     (start-world! worldref))))

(defn seesaw-watcher [playerid  _ __ new]
  (println "seesaw-watcher:" playerid))

(defn -main [ & args]
  (let [ wid (create-world)
        worldref (get-world wid)]
    (add-randomai-player! worldref :p1 "Alpha"  seesaw-watcher prototypes)
    (add-randomai-player! worldref :p2 "Beta"   seesaw-watcher prototypes)
    (add-randomai-player! worldref :p3 "Gamma"  seesaw-watcher prototypes)
    (draw-frame worldref :p1)
    ))