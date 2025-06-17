(ns task
  (:require
   [shadow.cljs.devtools.api :as shadow]
   [clojure.java.io :as io]
   [clojure.java.process :as process]))

(defonce css-watch-process (atom nil))

(defn shadow-watch-start []
  (shadow/watch :app))

(defn shadow-watch-stop []
  (shadow/stop-worker :app))

(defn node-ensure-deps []
  (let [node-modules-dir (io/file "node_modules")
        has-node-modules? (and
                           (.exists node-modules-dir)
                           (.isDirectory node-modules-dir))]
    (when-not has-node-modules?
      (let [opts {:out :inherit :err :inherit}
            cmd ["npm" "install"]
            proc (apply process/start opts cmd)]
        @(process/exit-ref proc)))))

(defn css-watch-start []
  (node-ensure-deps)
  (let [cmd ["npx" "@tailwindcss/cli"
             "-i" "./src/css/tailwind.css"
             "-o" "./target/classes/public/css/tailwind.css"
             "--watch"]
        opts {:out :inherit :err :inherit}
        process (apply process/start opts cmd)]
    (reset! css-watch-process process)))

(defn css-watch-stop []
  (when-some [css-watch @css-watch-process]
    (.destroyForcibly css-watch)
    (reset! css-watch-process nil)))

(defn watches-start []
  (shadow-watch-start)
  (css-watch-start)
  :started)

(defn watches-stop []
  (css-watch-stop)
  (shadow-watch-stop)
  :stopped)

(comment
  (watches-start)
  (watches-stop))
