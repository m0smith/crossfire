(ns crossfire.world)

(defn world-contains [world cood]
  (let [[width height] (:dim world)
        [x y] cood]
    (and (< x width) (< y height))))