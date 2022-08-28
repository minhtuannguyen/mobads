(ns sanitize
  (:require [babashka.fs :as fs]
            [babashka.process :refer [process]]
            [clojure.string :as str]))

(def DNS-FILE-NAME "dns.txt")

(defn read-dns-entries []
  (if (fs/exists? DNS-FILE-NAME)
    (fs/read-all-lines DNS-FILE-NAME)
    (prn "==> DNS file" DNS-FILE-NAME "doesn't exist")))

(defn dns-reachable? [entry]
  (prn ".... checking" entry)
  (->> (process ["nslookup" entry])
       :out
       slurp
       str/split-lines
       (filter #(str/starts-with? % "Address: "))
       (seq)))

(defn sanitize-dns [dns-entries]
  (filter dns-reachable? dns-entries))

(defn sanitize [& args]
  (let [actual-dns-entries (set (read-dns-entries))
        sanitized-dns-entries (some->> actual-dns-entries
                                       sanitize-dns
                                       set)]
    (when-not (= actual-dns-entries sanitized-dns-entries)
      (prn "some invalid dns entries has been found")
      (prn "Back up the old dns:" (str "old-" DNS-FILE-NAME))
      (fs/delete-if-exists (str "old-" DNS-FILE-NAME))
      (fs/copy DNS-FILE-NAME (str "old-" DNS-FILE-NAME))
      (prn "sanitize the dns file")
      (fs/delete-if-exists DNS-FILE-NAME)
      (spit (fs/file DNS-FILE-NAME) (str/join "\n" sanitized-dns-entries)))))