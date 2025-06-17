(ns example.clj-fullstack.core-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [example.clj-fullstack.core :as sut])) ; system under test

(deftest a-test
  (testing "A passing test"
    (is (= 4 (+ 2 2)))))
