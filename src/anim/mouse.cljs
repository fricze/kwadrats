(ns anim.mouse
  (:require [anim.state :refer [state]]
            [anim.data :refer [rands-from-zero]]
            [anim.view :refer [canvas]]
            [anim.events :refer [listen-for-many!]]))

(listen-for-many!
 js/window
 {:mousemove (fn [e]
               (swap! state assoc :mouse-y (* 1 (- (aget e "y") (aget canvas "offsetTop") 250)))
               false)
  :click (fn [e]
           (swap! state assoc :randy-bullets (rands-from-zero 100 3))
           false)})
