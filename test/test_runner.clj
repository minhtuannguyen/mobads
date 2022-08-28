(ns test-runner
  (:require [clojure.test :as t]))

(require 'sanitize-test)

(defn run-tests [& args]
  (let [{:keys [:fail :error]} (t/run-tests 'sanitize-test)]
    (+ fail error)))