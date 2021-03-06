(ns clojure-scratchpad.core
  (:gen-class)
  (:import (java.util Random)
           (java.util.regex Pattern))
  (:require [clojure.string :as str]))

(defn weighted-select
  ([m ^Random rand]
   (if-not (seq m)
     nil
     (let [weights (map vector (keys m) (reductions + (vals m)))
           sel (.nextInt rand (reduce + (vals m)))]
       (first (first (drop-while #(<= (second %) sel) weights)))))))

(defn do-candidate-map
  [seed input lvl]
  (if (= 0 lvl)
    (frequencies (drop 1 input))
    (->> (drop (if (str/starts-with? input seed) 0 1)
               (str/split input (re-pattern (Pattern/quote seed))))
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
  [input lvl length rand]
  (when (>= lvl (count input))
     (throw (IllegalArgumentException. "Level cannot be greater than input size")))
  (loop [output (transient [])
        seed (initial-seed input lvl rand)]
    (if (= (count output) length)
      (apply str (persistent! output))
      (if-let [c (weighted-select (candidate-map seed input lvl) rand)]
        (recur (conj! output c) (rotate-seed seed c))
        (recur (conj! output \u0000) (initial-seed input lvl rand))))))

(defn -main
  [in out level length & args]
  (spit out (random-writer (slurp in) (Long/parseLong level) (Long/parseLong length) (Random.))))