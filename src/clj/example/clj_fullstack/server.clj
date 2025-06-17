(ns example.clj-fullstack.server
  (:require
   [clojure.tools.logging :as log]
   [ring.adapter.jetty :as jetty]
   [ring.util.response :as resp]
   [ring.middleware.resource :refer [wrap-resource]]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.not-modified :refer [wrap-not-modified]]))

(def resource-root "public")

(defn index [request]
  (-> (resp/resource-response "index.html" {:root resource-root})
      (resp/content-type "text/html")))

(def handler
  (-> index
      (wrap-resource resource-root)
      (wrap-content-type)
      (wrap-not-modified)))

(defn start []
  (let [opts {:port 8080
              :join? false}]
    (log/info "Starting server")
    (jetty/run-jetty handler opts)))

(defn stop [server]
  (log/info "Stopping server")
  (.stop server))

(comment
  (def server (start))
  (stop server))
