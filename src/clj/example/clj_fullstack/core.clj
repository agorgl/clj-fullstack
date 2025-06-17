(ns example.clj-fullstack.core
  (:require
   [clojure.tools.logging :as log]
   [example.clj-fullstack.server :as server])
  (:gen-class))

(defn add-shutdown-hook [f]
  (let [shutdown-fn
        (fn []
          (f)
          (shutdown-agents))]
    (-> (Runtime/getRuntime)
        (.addShutdownHook
         (Thread. shutdown-fn)))))

(defn -main [& args]
  (log/info "Hello there")
  (let [server (server/start)]
    (add-shutdown-hook #(server/stop server))))
