(ns clojure-lwjgl.component)

(defprotocol Component
  (handle-input [component input-state])
  (get-visual [component]))

