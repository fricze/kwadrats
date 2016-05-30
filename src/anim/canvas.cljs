(ns anim.canvas)

;; Drawing helpers
(defn rad [degree]
  (* degree (/ (aget js/Math "PI") 180)))

(defn rectangle [ctx x1 y1 x2 y2]
  (.fillRect ctx x1 y1 x2 y2))

(defn rgb [r g b]
  (str "rgb("r","g","b")"))

(defn clear-canvas [ctx canvas-size old-color]
  (aset ctx "fillStyle" (rgb 60 60 60))
  (rectangle ctx 0 0 (:width canvas-size) (:height canvas-size))
  (aset ctx "fillStyle" old-color))

(defn animation-frame [fn]
  (.requestAnimationFrame js/window fn))
