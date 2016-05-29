(ns anim.draw
  (:require [anim.canvas :refer [fill-rect rgb]]))

(defn draw-bullets [ctx point-1-y point-2-y point-3-y]
  (.save ctx)
  (aset ctx "fillStyle" (rgb 230 230 230))
  (apply fill-rect ctx (assoc x 1 point-1-y))
  (apply fill-rect ctx (assoc x 1 point-2-y))
  (apply fill-rect ctx (assoc x 1 point-3-y))
  (.restore ctx))


(defn draw-targets [ctx]
  (apply fill-rect ctx (assoc y 1 (+ 0 target-1-y)))
  (apply fill-rect ctx (assoc y 1 (+ 0 target-2-y)))
  (apply fill-rect ctx (assoc y 1 (+ 0 target-3-y))))
