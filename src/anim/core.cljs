(ns anim.core
  (:require [cljs.core.async :as async :refer [<! >! timeout chan take!]]
            [anim.dom :refer [by-id create-element append!]]
            [anim.view :refer [body canvas canvas-size]]
            [anim.state :refer [randy-bullets counter]]
            [anim.data :refer [rand-from-zero rands-from-zero]]
            [anim.canvas :refer [fill-rect rgb animation-frame clear-canvas]]
            [anim.draw :refer [draw-bullets]]
            [anim.steps :refer [steps-list]])

  (:require-macros
   [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(append! body canvas)

;; Canvas setup
(def ctx (.getContext canvas "2d"))

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

(def points (atom 0))

(defn draw-game [up-down old-hit rands randy-targets x y]
  (let [g @mouse-y
        target-y 0
        xx (nth x 0)
        xy (nth y 0)
        up-down (if (> up-down 0.5) 0 230)
        target-1-y (+ up-down (nth randy-targets 0) (nth x 1))
        target-2-y (+ up-down (nth randy-targets 1) (- (nth x 1) 100))
        target-3-y (+ up-down (nth randy-targets 2) (+ 80 (nth x 1)))

        bullet-1-y (+ 80 (nth rands 0) (nth x 1) g)
        bullet-2-y (+ 80 (nth rands 1) (- (nth x 1) 100) g)
        bullet-3-y (+ 80 (nth rands 2) (+ 80 (nth x 1)) g)]

    (animation-frame (fn []
                       (clear-canvas ctx canvas-size (rgb 30 30 30))

                       ;; Bullets
                       (.save ctx)
                       (aset ctx "fillStyle" (rgb 230 230 230))
                       (apply fill-rect ctx (assoc x 1 bullet-1-y))
                       (apply fill-rect ctx (assoc x 1 bullet-2-y))
                       (apply fill-rect ctx (assoc x 1 bullet-3-y))
                       (.restore ctx)

                       ;; Targets
                       (apply fill-rect ctx (assoc y 1 (+ 0 target-1-y)))
                       (apply fill-rect ctx (assoc y 1 (+ 0 target-2-y)))
                       (apply fill-rect ctx (assoc y 1 (+ 0 target-3-y)))

                       (doseq [hit (filter identity (flatten old-hit))]
                         (.save ctx)
                         (aset ctx "fillStyle" (rgb 60 60 60))
                         (apply fill-rect ctx (assoc y 1 (+ hit)))
                         (.restore ctx))))


    ;; Check is someone whas hit?
    (let
        [hit (when (= xx (+ -20 (/ (:width canvas-size) 2)))
               (for [bullet [bullet-1-y bullet-2-y bullet-3-y]]
                 (for [target [target-1-y target-2-y target-3-y]]
                   (when (some #{bullet} (range (- target 20) (+ target 20)))
                     target))))]

      (when (seq hit) (doseq [nb (filter identity (flatten hit))]
                        (swap! points inc)))

      (if (seq hit) hit old-hit))))

(defn game-finished? [counter]
  (= @counter 20))

(defn game-finish-info! [counter]
  (when (game-finished? counter)
    (js/alert (str "You’ve got " 0 " points after 20 attacks!"))
    (js/alert (str "Just joking. You have " @points "! Poor! Play again!"))

    (reset! counter 0)
    (reset! points 0)))

(go
  (while true
    (let [#_(rand-times [(- (* 0.13 (.random js/Math)) 0.13)
                         (- (* 0.13 (.random js/Math)) 0.13)])
          ;; rand times gonna be useful later
          up-down (.random js/Math)]

      (doseq [steps steps-list]
        (reset! randy-bullets (rands-from-zero 100 3))

        (let [randy-targets (rands-from-zero 140 3)]
          (loop [step steps
                 hit []
                 t 36
                 i 1]
            (if-not (seq step)
              0
              (do (<! (timeout t))
                  (recur (rest step)
                         (let [step (first step)]
                           (draw-game
                            up-down hit
                            @randy-bullets randy-targets
                            step
                            (assoc step 0 (- (- (:width canvas-size) 20) (nth step 0)))))

                         (if (< i 80)
                           (* 0.98 t)
                           (* 1.02 t))

                         (inc i))))))

        (aset (by-id "points") "innerHTML" @points)
        (swap! counter inc)))

    (game-finish-info! counter)))
