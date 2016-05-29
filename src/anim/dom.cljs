(ns anim.dom)

;; DOM functions
(defn by-id [id]
  (.getElementById js/document id))

(defn create-element [name]
  (.createElement js/document name))

(defn append! [parent child]
  (.appendChild parent child))
