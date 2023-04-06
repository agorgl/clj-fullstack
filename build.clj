(ns build
  (:refer-clojure :exclude [test])
  (:require [clojure.tools.build.api :as b]))

(def lib 'net.clojars.agorgl/clj-fullstack)
(def version "0.1.0-SNAPSHOT")
(def main 'agorgl.clj-fullstack.core)
(def class-dir "target/classes")

(defn test "Run all the tests." [opts]
  (let [basis    (b/create-basis {:aliases [:test]})
        cmds     (b/java-command
                  {:basis     basis
                   :main      'clojure.main
                   :main-args ["-m" "cognitect.test-runner"]})
        {:keys [exit]} (b/process cmds)]
    (when-not (zero? exit) (throw (ex-info "Tests failed" {}))))
  opts)

(defn resources "Build resources." [_]
  (b/delete {:path "resources/public/js/compiled"})
  (b/process {:command-args ["npx" "shadow-cljs" "release" "app"]}))

(defn- uber-opts [opts]
  (assoc opts
         :lib lib :main main
         :uber-file (format "target/%s-%s.jar" lib version)
         :basis (b/create-basis {})
         :class-dir class-dir
         :src-dirs ["src/clj"]
         :ns-compile [main]))

(defn uber "Build the uberjar." [opts]
  (b/delete {:path "target"})
  (println "Building resources...")
  (resources opts)
  (let [opts (uber-opts opts)]
    (println "Copying source...")
    (b/copy-dir {:src-dirs ["resources" "src/clj"] :target-dir class-dir})
    (println (str "Compiling " main "..."))
    (b/compile-clj opts)
    (println "Building JAR...")
    (b/uber opts))
  opts)

(defn ci "Run the CI pipeline of tests (and build the uberjar)." [opts]
  (test opts)
  (uber opts))
