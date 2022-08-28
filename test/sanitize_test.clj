(ns sanitize-test
    (:require [clojure.test :refer [deftest is]]
              [sanitize :as sanitize]))

(deftest read-dns-entries-should-return-entries
         (is (not (zero? (count (sanitize/read-dns-entries))))))


(deftest should-remove-invalid-entries
         (is (= ["adx.adnxs.com"]
                (sanitize/sanitize-dns ["adx.adnxs.com"
                                        "bsx.bs.xxxxx"]))))