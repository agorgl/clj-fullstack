(ns agorgl.clj-fullstack
  (:require
   [clojure.string :as str]
   [clojure.edn :as edn]
   [clojure.java.shell :refer [sh]]))

(def node-deps
  ["npm-run-all"
   "shadow-cljs"
   "react"
   "react-dom"
   "tailwindcss"
   "@babel/cli"
   "@babel/core"
   "@babel/preset-env"
   "@babel/preset-react"
   "@babel/preset-typescript"])

(def clj-deps
  ["aero/aero"
   "integrant/integrant"
   "integrant/repl"
   "io.pedestal/pedestal.jetty"
   "ch.qos.logback/logback-classic"])

(def cljs-deps
  ["reagent"
   "re-frame"])

(defn dep-name [dep]
  (let [[s1 s2 :as name-parts]
        (-> dep
            (str/replace "@" "")
            (str/split #"/"))]
    (cond
      (= (count name-parts) 1) s1
      (= s1 s2) s1
      (str/includes? s1 ".") s2
      :else (str s1 "-" s2))))

(defn data-fn
  "Result is merged onto existing options data."
  [data]
  (let [versions
        (->> (concat
              (for [d node-deps]
                [(keyword "deps" (dep-name d))
                 (str/trim-newline
                  (:out (sh "npm" "view" d "version")))])
              (for [d clj-deps]
                [(keyword "deps" (dep-name d))
                 (-> (:out (sh "clojure" "-X:deps" "find-versions" ":lib" d ":n" "1"))
                     (edn/read-string)
                     :mvn/version)])
              (for [d cljs-deps]
                [(keyword "deps" (dep-name d))
                 (-> (:out (sh "clojure" "-X:deps" "find-versions" ":lib" d ":n" "1"))
                     (edn/read-string)
                     :mvn/version)]))
             (into {}))]
    (merge data versions)))

(defn template-fn
  "Example template-fn handler.
   Result is used as the EDN for the template."
  [edn data]
  ;; must return the whole EDN hash map
  (println "template-fn returning edn")
  edn)

(defn post-process-fn
  "Example post-process-fn handler.
   Can programmatically modify files in the generated project."
  [edn data]
  (println "post-process-fn not modifying" (:target-dir data)))
