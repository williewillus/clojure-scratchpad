(ns clojure-scratchpad.tcc)

(defn mask-email [^String email]
  (let [split (clojure.string/split email #"@")]
    (str
      (ffirst split)
      "*****"
      (last (first split))
      "@"
      (second split))))

(defn mask-phone [^String phone]
  (let [raw (clojure.string/replace phone #"\(|\)|\+|-|\s" "")
        intl (> (count raw) 10)]
    (println raw)
    (str
      (if intl (str "+" (apply str (repeat (- (count raw) 10) "*"))  "-") "")
      "***-***-"
      (subs raw (- (count raw) 4) (count raw)))))

(doseq [^String input (line-seq (java.io.BufferedReader. *in*))]
  (println
    (cond
      (.startsWith input "P:") (str "P:" (mask-phone (subs input 2)))
      (.startsWith input "E:") (str "E:" (mask-email (subs input 2)))
      :else "Unknown input")))
