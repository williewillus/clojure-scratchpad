(ns clojure-scratchpad.scraper.parser
  (:require [clojure.string :as str]))

(defn- long-or-nil [s]
  (try
    (Long/parseLong s)
    (catch NumberFormatException _ nil)))

(defn- parse-sem [s]
  (case s "2" :spring "6" :summer "9" :fall))

(def day-res {:monday #"M"
              :tuesday #"T(?!H)"
              :wednesday #"W"
              :thursday #"TH"
              :friday #"F"})

(defn- parse-days [s]
  (->> (keys day-res)
       (filter #(re-find (get day-res %) s))
       (into #{})))

(defn- parse-xlists [s]
  (->> (str/split s #",")
       (map str/trim)
       (map long-or-nil)
       (filter some?)
       (into #{})))

(defn- parse [line]
  (let [[yr sem dept-abbr dept-name course-num topic
         uuid sect-num title inst inst-eid inst-email
         days from to bldg room max-enrl seats-taken
         xlist-ct xlist-ptr xlists]
        (map str/trim (str/split line #"\t+"))]
    {:year (Long/parseLong yr)
     :sem (parse-sem sem)
     :dept-abbr dept-abbr
     :dept-name dept-name
     :course-num course-num
     :topic (Long/parseLong topic)
     :uuid (Long/parseLong uuid)
     :sect-num (Long/parseLong sect-num)
     :title title
     :inst inst
     :inst-eid inst-eid
     :inst-email inst-email
     :days (parse-days days)
     :start-time (long-or-nil from)
     :end-time (long-or-nil to)
     :bldg bldg
     :room room
     :max-enrl (Long/parseLong max-enrl)
     :seats-taken (Long/parseLong seats-taken)
     :x-list-ct (long-or-nil xlist-ct)
     :x-list-ptr (long-or-nil xlist-ptr)
     :x-lists (parse-xlists xlists)}))

(defn main []
  (let [f (clojure.java.io/reader "Current_Semester_Report.txt")
        lines (drop 33 (line-seq f))                        ; todo unhardcode
        data (doall (map parse lines))]
    (println "Parsed" (count data) "classes from text file")
    (take 20 (for [c data :when (seq (:x-lists c))]
               c))))