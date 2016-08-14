(ns clojure-scratchpad.core
  (:gen-class)
  (:import (java.util Random)
           (java.util.regex Pattern)))

(defn do-weighted-select
  ([m rand]
   (let [weights (zipmap (keys m) (reductions + (vals m))) ; TODO Not guaranteed to be ordered!
         sel (.nextInt rand (second (last weights)))]
     (key (first (drop-while #(<= (val %) sel) weights))))))

(defn weighted-select
  [m rand]
  (if-not (seq m)
    nil
    (do-weighted-select (filter #(> (val %) 0) m) rand)))

(defn do-candidate-map
  [seed input lvl]
  (if (= 0 lvl)
    (frequencies (drop 1 input))
    (->> (re-seq (re-pattern (str (Pattern/quote seed) "(.)")) input)
         (map second)
         (filter not-empty)
         (map first)
         (frequencies))))

(def memo-candidate-map (memoize do-candidate-map))

(defn candidate-map
  [seed input lvl]
  (if (<= lvl 6)
    (memo-candidate-map seed input lvl)
    (do-candidate-map seed input lvl)))

(defn rotate-seed
  [seed newchar]
  (if (empty? seed)
    ""
    (str (subs seed 1) newchar)))

(defn initial-seed
  [input lvl ^Random rand]
  (if (= 0 lvl)
    ""
    (let [start (.nextInt rand (- (count input) lvl))]
      (subs input start (+ start lvl)))))

(defn random-writer
  [input lvl length]
  (when (>= lvl (count input))
     (throw (IllegalArgumentException. "Level cannot be greater than input size")))
  (loop [output []
        ctr 0
        rand (Random.)
        seed (initial-seed input lvl rand)]
    (if (= ctr length)
      (apply str output)
      (if-let [c (weighted-select (candidate-map seed input lvl) rand)]
        (recur (conj output c) (inc ctr) rand (rotate-seed seed c))
        (recur (conj output \u0000) (inc ctr) rand (initial-seed input lvl rand))))))

(defn -main
  [in out level length & args]
  (spit out (random-writer (slurp in) (Integer/parseInt level) (Integer/parseInt length))))

