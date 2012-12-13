(ns crossfire.player.randomai
  (:use [crossfire.board :only [empty-locations get-board opponent-dictionary]]
        [crossfire.util :only [random-element]]
        [crossfire.world :only [active-world?]]
        [crossfire.player :only [place-piece! add-player! active-opponents-of player-turn?
                                 make-move!]]))

(defn randomly-place-piece! [worldref playerid piece-template]
  (let [random-locs (shuffle (empty-locations (get-board worldref playerid)))
        pp! (partial place-piece! worldref playerid piece-template) ]
    (loop [[start & more] random-locs]
      (if start
        (let [piece (pp! start)]
          (if piece
            piece
            (recur more)))))))

(defn randomai-watcher [f key _ __ new]
  (when (and (active-world? new)
             (player-turn? new key))
    (if-let [opponent (random-element (active-opponents-of new key))]
      (let [cood (random-element (empty-locations (get-board opponent) opponent-dictionary))
            ;huh? (println "randomai-watcher:" (:seqid new) key (opponent :playerid) cood )
            ]
        (f #(make-move! new key (:playerid opponent) cood))
        )))
)

(defn add-randomai-player! [worldref playerid name watcher templates]
  (add-player! worldref playerid name watcher)
  (doseq [tmpl templates]
    (randomly-place-piece! worldref playerid tmpl)))

