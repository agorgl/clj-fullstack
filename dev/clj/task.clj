(ns task
  (:require
   [shadow.cljs.devtools.api :as shadow]))

(defn shadow-watch-start []
  (shadow/watch :app))

(defn shadow-watch-stop []
  (shadow/stop-worker :app))

(defn watches-start []
  (shadow-watch-start)
  :started)

(defn watches-stop []
  (shadow-watch-stop)
  :stopped)

(comment
  (watches-start)
  (watches-stop))
