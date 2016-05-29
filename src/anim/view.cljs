(ns anim.view
  (:require [anim.dom :refer [by-id create-element]]))

;; body reference
(def body (by-id "anim-body"))

;; canvas element
(def canvas (create-element "canvas"))

(def canvas-size
  {:width 600
   :height 650})

(aset canvas "height" (:height canvas-size))
(aset canvas "width" (:width canvas-size))
