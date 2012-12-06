(ns crossfire.protocol.ui)

(defprotocol UI
  (init [ui world player]
    "Initialize the UI")
  (choose-shot [ui world player]
    "Return a vector with [opponent cood]"))