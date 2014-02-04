(ns anim.core
  (:require [cljs.core.async :as async :refer [<! >! <!! timeout chan alt! go]])
  (:require-macros
   [cljs.core.async.macros :refer [go]]))

;; Some weird shit
(enable-console-print!)

;; DOM functions
(defn by-id [id]
  (.getElementById js/document id))

(defn create-element [name]
  (.createElement js/document name))

(defn append! [parent child]
  (.appendChild parent child))

;; DOM refs
(def body (by-id "anim-body"))

;; DOM elements
(def canvas (create-element "canvas"))

(def canvas-attrs
  {:width 600
   :height 650})

(aset canvas "height" (:height canvas-attrs))
(aset canvas "width" (:width canvas-attrs))

;; DOM operations
(append! body canvas)

;; Canvas setup
(def ctx (.getContext canvas "2d"))

;; Data processing helpers
(defn lazy-nth [n sq]
  (take 1 (drop (dec n) sq)))

(defn rand-from-zero [from-zero] 
  (int (- (* from-zero (.random js/Math)) (/ from-zero 2))))

(defn rands-from-zero [from-zero how-many]
  (for [i (range how-many)]
    (rand-from-zero from-zero)))

;; Drawing helpers
(defn rad [degree]
  (* degree (/ (aget js/Math "PI") 180)))

(defn fillRect [ctx x1 y1 x2 y2]
  (.fillRect ctx x1 y1 x2 y2))

(def fillThisRect (partial fillRect ctx))

(defn rgb [r g b]
  (str "rgb("r","g","b")"))

(defn clear-canvas [old-color]
  (aset ctx "fillStyle" (rgb 60 60 60))
  (.fillRect ctx 0 0 (:width canvas-attrs) (:height canvas-attrs))
  (aset ctx "fillStyle" old-color))

(defn animation-frame [fn]
  (.requestAnimationFrame js/window fn))

;;Some actual drawing

(def mouse-x (atom 0))
(def mouse-y (atom 0))


(.addEventListener js/window 
                   "mousemove" 
                   (fn [e]
                     (reset! mouse-y (* 1 (- (aget e "y") (aget canvas "offsetTop") 250)))
                     false))

(def points (atom 0))

(defn some-shit2 [x i]
  (.moveTo ctx (+ i x) x)
  (.lineTo ctx (+ (* 0.5 x) x) (+ (+ (* 0.5 x) x) i))
  (.lineTo ctx (- x i) (* 2 x))
  (.lineTo ctx (- x (* 0.5 x)) (- (+ (* 0.5 x) x) i))
  (.lineTo ctx (+ i x) x)
  (.stroke ctx))

(defn twoRects [i hit! r r-gets x y]
  (let [g @mouse-y
        target-y 0
        xx (nth x 0)
        xy (nth y 0)
        up-down (if (> i 0.5) 0 230)
        target-1-y (+ up-down (nth r-gets 0) (nth x 1))
        target-2-y (+ up-down (nth r-gets 1) (- (nth x 1) 100))
        target-3-y (+ up-down (nth r-gets 2) (+ 80 (nth x 1)))
        
        bullet-1-y (+ 80 (nth r 0) (nth x 1) g)
        bullet-2-y (+ 80 (nth r 1) (- (nth x 1) 100) g)
        bullet-3-y (+ 80 (nth r 2) (+ 80 (nth x 1)) g)]
    

    (animation-frame (fn []
      (clear-canvas (rgb 30 30 30))
      
      ;; Bullets
      (.save ctx)
      (aset ctx "fillStyle" (rgb 230 230 230))
      (apply fillThisRect (assoc x 1 bullet-1-y))
      (apply fillThisRect (assoc x 1 bullet-2-y))
      (apply fillThisRect (assoc x 1 bullet-3-y))
      (.restore ctx)
      
      (apply fillThisRect (assoc y 1 (+ 0 target-1-y)))
      (apply fillThisRect (assoc y 1 (+ 0 target-2-y)))
      (apply fillThisRect (assoc y 1 (+ 0 target-3-y)))
      
      (doseq [hit (filter identity (flatten hit!))]
        (.save ctx)
        (aset ctx "fillStyle" (rgb 60 60 60))
        (apply fillThisRect (assoc y 1 (+ hit)))
        (.restore ctx))))
      
    (let 
      [hit (when (= xx (+ -20 (/ (:width canvas-attrs) 2)))
        (for [bullet [bullet-1-y bullet-2-y bullet-3-y]]
          (for [target [target-1-y target-2-y target-3-y]]
            (when (some #{bullet} (range (- target 20) (+ target 20)))
              target))))]
      
      (when (seq hit) (doseq [nb (filter identity (flatten hit))]
                        (swap! points inc)))
      
      (if (seq hit) hit hit!))))


(def steps 
  [(let [steps [0 170 20 20]]
         (for [i (range 1 160)]
           (assoc steps
             0 (+ 0 (* i 4)))))
   
   (let [steps [(- (:width canvas-attrs) 20) 170 20 20]]
         (for [i (range 1 160)]
           (assoc steps
             0 (- (nth steps 0) (* i 4)))))
   
   (let [steps [180 300 20 20]]
         (for [i (range 1 80 #_20)]
           (assoc steps
             0 (+ 180 (* i 1))
             1 (- 300 (* i 0.5))
             2 (- 20 (* i 1))
             3 (+ 20 (* i 1)))))])

(def counter (atom 0))

(go
  (dotimes [_ 1]
    
    (while true 
      (let [rand-times [(+ -0.035 (* 0.13 (.random js/Math)))
                        (+ -0.035 (* 0.13 (.random js/Math)))
                        (+ -0.035 (* 0.13 (.random js/Math)))
                        (+ -0.035 (* 0.13 (.random js/Math)))]
            x-ray (.random js/Math)]
        
        (let [rands (rands-from-zero 140 3)
            randy-targets (rands-from-zero 100 3)]
        (loop [step (nth steps 0) hit! [] t 20 i 1]
          (if-not (seq step)
            0
            (do (<! (timeout t))
              (recur (rest step)
                     (let [step (first step) i 1]
                       (twoRects x-ray hit! rands randy-targets step (assoc step 0 (- (- (:width canvas-attrs) 20) (nth step 0)))))
                     (if (< i 80)
                       (* (- 0.99 (nth rand-times 0)) t)
                       (* (- 0.99 (nth rand-times 1)) t))
                     (inc i))))))
      (aset (by-id "points") "innerHTML" @points)
      (swap! counter inc)
      
      (let [rands (rands-from-zero 140 3)
            randy-targets (rands-from-zero 100 3)] 
        (loop [step (nth steps 1) hit! [] t 20 i 1]
          (if-not (seq step)
            0
            (do (<! (timeout t))
              (recur (rest step)
                     (let [step (first step) i 2]
                       (twoRects x-ray hit! rands randy-targets step (assoc step 0 (- (- (:width canvas-attrs) 20) (nth step 0)))))
                     (if (< i 80)
                       (* (- 0.99 (nth rand-times 2)) t)
                       (* (- 0.99 (nth rand-times 3)) t))
                     (inc i))))))
      (aset (by-id "points") "innerHTML" @points))
      (swap! counter inc)
      
      (when (= @counter 20)
        (js/alert (str "Zdobyłaś/eś " 0 " punktów po 20 atakach!"))
        (js/alert (str "Żartowałem. Masz " @points "! Słabo! Zagraj jeszcze raz!"))
        (reset! counter 0)
        (reset! points 0))
      
      (<! (timeout 100)))

    (<! (timeout 200))))

(println "Hello world!")
