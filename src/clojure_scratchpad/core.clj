(ns clojure-scratchpad.core
  (:gen-class)
  (:import (java.util Random)
           (java.util.regex Pattern)))

(defn candidate-map
  [seed input lvl]
  (if (= 0 lvl)
    (frequencies (drop 1 input))
    (->> (re-seq (re-pattern (str (Pattern/quote seed) "(.)")) input)
         (map second)
         (filter not-empty)
         (map first)
         (frequencies))))

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
  input)

(defn -main
  [in out level length & args]
  (spit out (random-writer (slurp in) (Integer/parseInt level) (Integer/parseInt length))))

