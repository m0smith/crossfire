(ns crossfire.protocol.ui)

(defprotocol UI
  (choose-shot [ui world player]
    "Return a vector with [opponent cood]"))