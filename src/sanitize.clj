(ns sanitize
  (:require [babashka.fs :as fs]
            [babashka.process :refer [process]]
            [clojure.string :as str]))

(def DNS-FILE-NAME "dns.txt")

(defn read-dns-entries []
  (if (fs/exists? DNS-FILE-NAME)
    (fs/read-all-lines DNS-FILE-NAME)
    (println "==> DNS file" DNS-FILE-NAME "doesn't exist")))

(defn dns-reachable? [entry]
  (let [reachable? (->> (process ["nslookup" entry])
                        :out
                        slurp
                        str/split-lines
                        (filter #(str/starts-with? % "Address: "))
                        empty?
                        not)]
    (println ".... checking " entry " is reachable?: " reachable?)
    reachable?))

(defn sanitize-dns [dns-entries]
  (filter dns-reachable? dns-entries))

(defn sanitize [& args]
  (let [actual-dns-entries (set (read-dns-entries))
        sanitized-dns-entries (some->> actual-dns-entries
                                       sanitize-dns
                                       set)]
    (if (= actual-dns-entries sanitized-dns-entries)
      (println "===> The DNS list is ok")
      (do
        (println "some invalid dns entries has been found")
        (println "Back up the old dns: " (str "old-" DNS-FILE-NAME))
        (fs/delete-if-exists (str "old-" DNS-FILE-NAME))
        (fs/copy DNS-FILE-NAME (str "old-" DNS-FILE-NAME))
        (println "sanitize the dns file")
        (fs/delete-if-exists DNS-FILE-NAME)
        (spit (fs/file DNS-FILE-NAME) (str/join "\n" sanitized-dns-entries))))))