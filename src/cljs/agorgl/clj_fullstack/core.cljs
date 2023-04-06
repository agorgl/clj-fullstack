(ns agorgl.clj-fullstack.core)

(defn ^:dev/after-load mount-root []
  (let [root-el (.getElementById js/document "app")]
    (set! (.-innerHTML root-el) "Hello there")))

(defn init []
  (mount-root))
