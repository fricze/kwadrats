(ns anim.state)

(def state
  (atom {:random-bullets 0
         :counter 0
         :step-delay 30
         :points 0

         :mouse-x 0
         :mouse-y 0

         :bullets []
         :targets []

         :bullet-step nil
         :target-step nil}))
