(ns crossfire.util)

(defn random-element [coll]
  (when (seq coll) (rand-nth coll)))

(defn threadid []
  (. (Thread/currentThread) getName)
  )
