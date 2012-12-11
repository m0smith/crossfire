(ns crossfire.player.randomai
  (:use [crossfire.board :only [empty-locations get-board ]]
        [crossfire.player :only [place-piece! add-player!]]))

(defn randomly-place-piece! [worldref playerid piece-template]
  (let [random-locs (shuffle (empty-locations (get-board worldref playerid)))
        pp! (partial place-piece! worldref playerid piece-template) ]
    (loop [[start & more] random-locs]
      (if start
        (let [piece (pp! start)]
          (if piece
            piece
            (recur more)))))))

(defn randomai-watcher [key _ __ new]
  (println "randomai-watcher:" key new))

(defn add-randomai-player! [worldref playerid name watcher templates]
  (add-player! worldref playerid name watcher)
  (doseq [tmpl templates]
    (randomly-place-piece! worldref playerid tmpl)))

