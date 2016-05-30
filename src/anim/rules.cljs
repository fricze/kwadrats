(ns anim.rules)

(defn game-finished? [counter]
  (= counter 20))

(defn game-finish-info! [state]
  (let [{:keys [counter points]} @state]
    (when (game-finished? counter)
      (js/alert (str "You’ve got " 0 " points after 20 attacks!"))
      (js/alert (str "Just joking. You have " points "! Poor! Play again!"))

      (swap! state assoc :counter 0 :points 0))))
