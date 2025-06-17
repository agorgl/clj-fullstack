(ns build
  (:refer-clojure :exclude [test])
  (:require
   [clojure.tools.build.api :as b]))

(def lib 'net.clojars.example/clj-fullstack)
(def version "0.1.0-SNAPSHOT")
(def main 'example.clj-fullstack.core)
(def class-dir "target/classes")

(defn clean
  "Remove the target directory"
  [opts]
  (b/delete {:path "target"})
  opts)

(defn test
  "Run all the tests"
  [opts]
  (let [basis (b/create-basis {:aliases [:test]})
        cmds (b/java-command
              {:basis basis
               :main 'clojure.main
               :main-args ["-m" "cognitect.test-runner"]})
        {:keys [exit]} (b/process cmds)]
    (when-not (zero? exit)
      (throw (ex-info "Tests failed" {}))))
  opts)

(defn cljs
  "Build the cljs sources"
  [opts]
  (let [basis (b/create-basis {:aliases [:cljs]})
        cmds (b/java-command
              {:basis basis
               :main 'clojure.main
               :main-args ["-m" "shadow.cljs.devtools.cli" "release" "app"]})
        {:keys [exit]} (b/process cmds)]
    (when-not (zero? exit)
      (throw (ex-info "Building cljs failed" {}))))
  opts)

(defn node-dependencies
  "Install node dependencies"
  [opts]
  (let [cmds {:command-args ["npm" "install"]}
        {:keys [exit]} (b/process cmds)]
    (when-not (zero? exit)
      (throw (ex-info "Installing node dependencies failed" {}))))
  opts)

(defn css
  "Build the css sources"
  [opts]
  (node-dependencies opts)
  (let [cmds {:command-args ["npx" "@tailwindcss/cli"
                             "-i" "./src/css/tailwind.css"
                             "-o" "./target/classes/public/css/tailwind.css"
                             "--minify"]}
        {:keys [exit]} (b/process cmds)]
    (when-not (zero? exit)
      (throw (ex-info "Building css failed" {}))))
  opts)

(defn resources
  "Build the resources"
  [opts]
  (cljs opts)
  (css opts)
  opts)

(defn- uber-opts [opts]
  (assoc opts
         :lib lib
         :main main
         :uber-file (format "target/%s-%s.jar" lib version)
         :basis (b/create-basis {})
         :class-dir class-dir
         :src-dirs ["src/clj"]
         :ns-compile [main]))

(defn uber
  "Build the uberjar"
  [opts]
  (clean opts)
  (let [opts (uber-opts opts)]
    (println "Building resources...")
    (resources opts)
    (println "Copying source...")
    (b/copy-dir {:src-dirs ["resources" "src/clj"] :target-dir class-dir})
    (println (str "Compiling " main "..."))
    (b/compile-clj opts)
    (println "Building JAR...")
    (b/uber opts))
  opts)

(defn ci
  "Run the CI pipeline of tests (and build the uberjar)"
  [opts]
  (test opts)
  (uber opts)
  opts)
