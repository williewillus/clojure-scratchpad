; Kahn 1962
(ns clojure-scratchpad.tsort
  (:import (clojure.lang PersistentQueue)))

(def graph {:a #{:b :c}, :c #{:d :a}, :b #{:d}, :e #{:a :b}})

(defn- dependents [g to-find]
  (for [[k v] g :when (contains? v to-find)]
    k))

(defn- remove-edge [g from to]
  (update g from #(disj % to)))

(defn tsort [g]
  (loop [wg g
         result []
         s (into PersistentQueue/EMPTY (filter #(empty? (dependents g %)) (keys g)))]
    (if (seq s)
      (let [n (peek s)
            dependencies (get wg n)
            new-graph (reduce #(remove-edge %1 n %2) wg dependencies)
            to-add (filter #(empty? (dependents new-graph %)) dependencies)]
        (recur new-graph (conj result n) (apply conj (pop s) to-add)))
      (if (every? empty? (vals wg))
        result
        (throw (IllegalArgumentException. "Detected cycle in graph"))))))
