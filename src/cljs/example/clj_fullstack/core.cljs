(ns example.clj-fullstack.core)

(defn render []
  (js/console.log "render"))

;; called once on page load via :init-fn in build config
(defn init []
  (js/console.log "init")
  (render))

;; called after every hot-reload
(defn ^:dev/after-load reload! []
  (js/console.log "reload")
  (render))
