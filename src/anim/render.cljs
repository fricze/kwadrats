(ns anim.render
  (:require [anim.view :refer [canvas canvas-size ctx points]]
            [anim.state :refer [state]]
            [anim.canvas :refer [rectangle rgb animation-frame clear-canvas]]
            [anim.rules :refer [game-finish-info!]]))

(enable-console-print!)

(defn animate [{:keys [bullets bullet-step targets target-step hit]}]
  (clear-canvas ctx canvas-size (rgb 30 30 30))

  ;; Bullets
  (.save ctx)
  (aset ctx "fillStyle" (rgb 230 230 230))
  (doseq [bullet bullets]
    (apply rectangle ctx (assoc bullet-step 1 bullet)))
  (.restore ctx)

  ;; Draw targets
  (doseq [target (filter
                  ;; filter out hit targets
                  (complement (into #{} hit))
                  targets)]
    (apply rectangle ctx (assoc target-step 1 target))))

(add-watch
 state :watcher
 (fn [key atom old-state new-state]
   (animation-frame
    (fn []
      (aset points "innerHTML" (:points new-state))
      (game-finish-info! state)

      (animate new-state)))))
