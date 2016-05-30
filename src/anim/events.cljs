(ns anim.events)

(defn listen! [target event handler]
  (.addEventListener target (name event) handler))

(def listen-once! (memoize listen!))

(defn listen-for-many! [target event-with-handlers]
  (doseq [[event handler] (seq event-with-handlers)]
    (listen-once! target event handler)))
