(ns crossfire.ui.seesaw
  (:use [seesaw core graphics]
        [crossfire.player.randomai :only [ add-randomai-player! randomai-watcher]]
        [crossfire.world :only [create-world get-world start-world!]]
        [crossfire.board :only [all-location-cvals display-dictionary player-dictionary
                                opponent-dictionary get-dimensions
                                get-board]]
        [crossfire.player :only [get-player all-players opponents-of make-move!]]))

(def prototypes [ [[0 0] [0 1]]
                  [[0 0] [1 0] [2 0]]
                  [[0 0] [1 0] [1 1]]])

(def root-frame (frame :size [500 :by 500]))

(def class-dictionary
  {:open :grey
   :hit :red
   :miss :white
   :empty :blue})

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

(defn build-id [[x y :as cood] playerid]
  (str (name playerid) "-" x "-" y))

(defn peg-widget [[x y :as cood] tile player dictionary world]
  (let [ playerid (:playerid player)]
    (button
     :id (build-id cood playerid)
     ;;:border (str cood)
     :class tile
     :user-data {:cood cood
                 :opponentid playerid
                 :worldid (:worldid world)
                 }
     :location [(* tile-size x) (* tile-size y)]
     :bounds [(* tile-size x) (* tile-size y) tile-size tile-size]
     :text (dictionary tile))))

;; (defn player-board-tiles [world player]
;;   (for [[cood tile] (player-board-locations world player)]
;;     (peg-widget cood tile player nil)))

(defn board-tiles* [world player dictionary]
  (let [cvals (all-location-cvals (get-board player) dictionary)]
    (for [[cood value] cvals]
      (do
        (peg-widget cood value player display-dictionary world)))))

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

(defn handle-button [playerid event]
  (let [user-data (user-data event)]
    (make-move! (deref (get-world (:worldid user-data)))
                playerid
                (:opponentid user-data)
                (:cood user-data)
                )))

(defn add-behaviors [root playerid]
  (println "add-behaviors start" )
  (config! (select root [:.open])
           :background :grey
           :listen [:mouse-pressed (partial handle-button playerid)])
  (config! (select root [:.empty])
           :background :blue
           :listen [:mouse-pressed (partial handle-button playerid)])
  (config! (select root [:.hit]) :background :red)
  (config! (select root [:.miss]) :background :white)
  (println "add-behaviors end")
  root)

(defn panels [world player]
  (let [opponents (opponents-of world (:playerid player))]
    [ (player-panels world (map (partial get-player world) (map :playerid opponents)) player-dictionary)
      (player-panels world [player] player-dictionary)]))

(defn do-after "Wait for 'delay' before executing the function 'f'"
  [delay f]
  (let [executor (java.util.concurrent.Executors/newSingleThreadScheduledExecutor)]
    (.schedule executor f delay java.util.concurrent.TimeUnit/MILLISECONDS)))

(defn draw-frame [worldref playerid]
  
  (let [world @worldref
        player (get-player world playerid)]
    (invoke-later
     (->
      root-frame
      (config!  :title "Crossfire",
                :content (border-panel
                          :vgap 5
                          :hgap 5
                          :north "Take Aim!"
                          :center (horizontal-panel :items (panels world player))
                          :south (label :id :status-bar :text "Status Bar")
                          )
                ,
                :on-close :exit)
      (add-behaviors playerid)
      pack!
      show!)
     (println "drew-frame")
     
     (send-off (agent worldref) #(start-world! %)))))


(defn move-result-str [move-result]
  (str (:playerid move-result) " shot at " (:opponentid move-result) " => "
       (:opponent-state move-result)))

(defn seesaw-watcher [watchid  _ __ new]
  (let [players (all-players new)]
     (doseq [player players]
       (let [playerid (:playerid player)
             board (get-board player)
             cvals (all-location-cvals board)]
         (invoke-later
          (doseq [[cood value] cvals]
            (let [widget (select root-frame [(str "#" (build-id cood playerid))])]
              (config! widget :text (display-dictionary value)
                       :class value
                       :background (class-dictionary value)))
            )
          )
         ;(add-behaviors root-frame)
         )
       ))
  (when (= :hit (get-in new [:move-result :result]))
    (config! (select root-frame [:#status-bar]) :text (move-result-str (:move-result new)))
    (println "seesaw-watcher:" watchid (:seqid new) (:move-result new))))




(defn -main [ & args]
  (let [ wid (create-world)
        worldref (get-world wid)]
    (add-randomai-player! worldref :p1 "Alpha" prototypes)
    (add-randomai-player! worldref :p2 "Beta"  prototypes)
    (add-randomai-player! worldref :p3 "Gamma" prototypes)
    (add-randomai-player! worldref :p4 "Delta" prototypes)
    ;;(add-watch worldref :p1 (partial randomai-watcher (partial do-after 500)))
    (add-watch worldref :p2 (partial randomai-watcher (partial do-after 500)))
    (add-watch worldref :p3 (partial randomai-watcher (partial do-after 500)))
    (add-watch worldref :p4 (partial randomai-watcher (partial do-after 500)))
    (add-watch worldref :observer seesaw-watcher)
    (draw-frame worldref :p1)
    ))