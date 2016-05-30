(ns anim.core
  (:require [cljs.core.async :as async :refer [<! >! timeout chan take!]]
            [anim.dom :refer [by-id create-element append!]]
            [anim.view :refer [body canvas canvas-size]]
            [anim.state :refer [state]]
            [anim.data :refer [rand-from-zero rands-from-zero in-range? reduce-points xoxo hits]]
            [anim.canvas :refer [rectangle rgb animation-frame clear-canvas]]
            [anim.draw :refer [draw-bullets]]
            [anim.steps :refer [steps-list]]
            [anim.mouse]
            [anim.rules :refer [game-finish-info!]])

  (:require-macros
   [cljs.core.async.macros :refer [go go-loop]]))

(append! body canvas)

;; Canvas setup
(def ctx (.getContext canvas "2d"))


(enable-console-print!)

(defn animate [{:keys [bullets x old-hit targets y]}]
  (println x)
  (println y)

  (animation-frame
   (fn []
     (clear-canvas ctx canvas-size (rgb 30 30 30))

     ;; Bullets
     (.save ctx)
     (aset ctx "fillStyle" (rgb 230 230 230))
     (doseq [bullet bullets]
       (apply rectangle ctx (assoc x 1 bullet)))
     (.restore ctx)

     ;; Draw targets
     (doseq [target (filter
                     ;; filter out hit targets
                     (complement (into #{} old-hit))
                     targets)]
       (apply rectangle ctx (assoc y 1 target))))))

(add-watch
 state :watcher
 (fn [key atom old-state new-state]
   (animate new-state)))

(defn multi-time [time] (* 5 time))

(defonce run-game!
  (memoize (fn []
             (go-loop [up-down (.random js/Math)]

               (doseq [steps steps-list]
                 (swap! state assoc :randy-bullets (rands-from-zero 100 3))

                 (let [randy-targets (rands-from-zero 140 3)]
                   (loop [step steps
                          hit []
                          ;; time for one step, used for bullets speed manipulation
                          t (multi-time 36)
                          i 1]

                     (if-not (seq step)
                       0
                       (do (<! (timeout t))
                           (recur
                            (rest step)

                            (let [step (first step)

                                  x step
                                  y (assoc step 0 (- (- (:width canvas-size) 20) (nth step 0)))

                                  rands (:randy-bullets @state)

                                  g (:mouse-y @state)
                                  xx (nth x 0)
                                  ;; xy (nth y 0)

                                  up-down (if (> up-down 0.5) 0 230)

                                  x1 (nth x 1)

                                  targets (xoxo randy-targets up-down x1)
                                  bullets (xoxo rands 140 g)]

                              (swap! state assoc
                                     :bullets bullets
                                     :targets targets

                                     :x x
                                     :y x

                                     :hit hit)

                              (hits canvas-size state bullets targets xx hit))

                            (if (< i 80)
                              (* 0.98 t)
                              (* 1.02 t))

                            (inc i))))))

                 (aset (by-id "points") "innerHTML" (:points @state))
                 (swap! state update :counter inc))

               (game-finish-info! state)

               (recur (.random js/Math))))))

(run-game!)
