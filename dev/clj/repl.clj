(ns repl
  (:require
   [clojure.java.io :as io]
   [shadow.cljs.devtools.server :as shadow-server]
   [task]))

(defn ensure-classpath-entry [path]
  (try
    (.mkdirs (io/file path))
    (let [loader (-> (Thread/currentThread)
                     (.getContextClassLoader))
          method (->> loader
                      (.getClass)
                      (.getSuperclass)
                      (.getDeclaredMethods)
                      (filter #(= (.getName %) "appendClassPath"))
                      (first))]
      (.setAccessible method true)
      (.invoke method loader (into-array Object [path])))
    (catch Exception e
      (binding [*out* *err*]
        (println (format "Could not add entry \"%s\" to classpath" path)))
      (.printStackTrace e))))

(defn -main [& args]
  (ensure-classpath-entry "target/classes")
  (shadow-server/start!)
  (task/watches-start))
