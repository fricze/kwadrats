(ns anim.steps
  (:require [anim.view :refer [canvas-size]]))

(defn steps [first]
  (let [steps
        ;; x, y, width, height
        [0 170 20 20]]
    (for [i (range 1 160)]
      (assoc steps
             0 (+ 0 (* i 4))))))

(def steps-list
  [(steps 0)
   (steps 0)])
