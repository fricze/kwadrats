(ns anim.data)

(defn rand-from-zero [from-zero]
  (int (* (inc from-zero) (.random js/Math))))

(defn rands-from-zero [from-zero length]
  (for [i (range length)]
    (rand-from-zero from-zero)))

(defn in-range? [number start stop]
  (and (> number start) (< number stop)))

(defn reduce-points
  [modifiers {:keys [shift targets], as coll} target]
  (let [new-target (apply + target shift modifiers)]
    (assoc coll
           :shift (+ 80 shift)
           :targets (conj targets
                          new-target))))

(defn xoxo [coll & modifiers]
  (:targets
   (reduce
    (partial reduce-points modifiers)
    {:shift -80
     :targets []}
    coll)))

(defn hits [canvas-size state bullets targets xx old-hit]
  (let
      [bullets-in-middle (= xx (- (/ (:width canvas-size) 2) 20))
       hit (when bullets-in-middle
             (for [bullet bullets
                   target targets
                   :let [bullet-target-collision
                         (in-range? bullet (- target 20) (+ target 20))]]
               (when bullet-target-collision target)))]

    (when (seq hit)
      (let [new-points (->>
                        (flatten hit)
                        (filter identity)
                        (count))]
        (swap! state update :points + new-points)))

    (if (seq hit) hit old-hit)))
