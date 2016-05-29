(ns anim.mouse)

;; Handle mouse position
(def mouse-x (atom 0))
(def mouse-y (atom 0))

(.addEventListener js/window
                   "mousemove"
                   (fn [e]
                     (reset! mouse-y (* 1 (- (aget e "y") (aget canvas "offsetTop") 250)))
                     false))

(.addEventListener js/window
                   "click"
                   (fn [e]
                     (reset! randy-bullets (rands-from-zero 100 3))
                     false))
