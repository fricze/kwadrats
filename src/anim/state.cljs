(ns anim.state)

;; Handle mouse position

(def state
  (atom {:randy-bullets 0
         :counter 0
         :step-delay 30
         :points 0

         :mouse-x 0
         :mouse-y 0

         :step []
         :hit []
         :t 36
         :i 1

         :bullets []
         :targets []

         :x nil
         :y nil}))
