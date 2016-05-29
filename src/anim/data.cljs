(ns anim.data)

(defn rand-from-zero [from-zero]
  (int (* (inc from-zero) (.random js/Math))))

(defn rands-from-zero [from-zero length]
  (for [i (range length)]
    (rand-from-zero from-zero)))
