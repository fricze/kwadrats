(ns anim.view
  (:require [anim.dom :refer [by-id create-element append!]]))

;; body reference
(def body (by-id "anim-body"))
(def points (by-id "points"))

;; canvas setup
(def canvas (create-element "canvas"))

(def ctx (.getContext canvas "2d"))

(def canvas-size
  {:width 600
   :height 650})

(aset canvas "height" (:height canvas-size))
(aset canvas "width" (:width canvas-size))

(append! body canvas)
