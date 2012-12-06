(ns crossfire.protocol.player)

(defprotocol Player
  (make-move [player world callback]))