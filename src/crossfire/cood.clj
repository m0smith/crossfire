(ns crossfire.cood)

(defn cood+
  ([] [])
  ([v] v)
  ([[a1 a2] [b1 b2]]
     [(+ a1 b1) (+ a2  b2)]))
