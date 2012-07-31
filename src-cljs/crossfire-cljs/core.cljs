(ns crossfire-cljs.core
  (:require [crossfire.core :as core]))

(defn set-html
  "Sets `.innerHTML` of the given tagert element to the give `html`"
  [ & html]
  (set! js/document.body.innerHTML (apply str "]]" html)))


(js/alert "Starting Crossfire!")
(binding [*print-fn*  #(.write js/document %)]
		 (core/-main))