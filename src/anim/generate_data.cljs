(ns anim.generate-data
  (:require [cljs.core.async :as async :refer [<! timeout]]
            [anim.view :refer [canvas-size]]
            [anim.state :refer [state]]
            [anim.data :refer [rands-from-zero in-range? calculate-points-position hits]]
            [anim.steps :refer [steps-list]])

  (:require-macros
   [cljs.core.async.macros :refer [go go-loop]]))

(defn multi-time [time] (* 1.5 time))

(defonce run-game!
  (memoize
   #(go-loop [up-down (.random js/Math)]

      (doseq [steps steps-list]
        (swap! state assoc :random-bullets (rands-from-zero 100 3))

        (let [random-targets (rands-from-zero 140 3)]
          (loop [step steps
                 hit []
                 ;; time for one step, used for bullets speed manipulation
                 step-time (multi-time 36)
                 i 1]

            (if-not (seq step)
              0
              (do (<! (timeout step-time))
                  (recur
                   (rest step)

                   (let [bullet-step (first step)

                         target-step (assoc bullet-step 0
                                            (- (- (:width canvas-size) 20) (nth bullet-step 0)))

                         random-bullets (:random-bullets @state)

                         up-down (if (> up-down 0.5) 0 230)

                         bullets-current-x (nth bullet-step 0)
                         bullets-current-y (nth bullet-step 1)

                         targets (calculate-points-position
                                  random-targets up-down bullets-current-y)
                         bullets (calculate-points-position
                                  random-bullets 140 (:mouse-y @state))]

                     (swap! state
                            assoc
                            :bullets bullets
                            :bullet-step bullet-step
                            :target-step target-step
                            :hit hit
                            :targets targets)

                     (hits canvas-size state bullets targets bullets-current-x hit))

                   (if (< i 80)
                     (* 0.98 step-time)
                     (* 1.02 step-time))

                   (inc i))))))

        (swap! state update :counter inc))
      (recur (.random js/Math)))))
