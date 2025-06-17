(ns repl
  (:require
   [shadow.cljs.devtools.server :as shadow-server]
   [task]))

(defn -main [& args]
  (shadow-server/start!)
  (task/watches-start))
