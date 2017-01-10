(ns clojure-scratchpad.scraper.parser
  (:require [clojure.string :as str]))

(defn- long-or-nil [s]
  (try
    (Long/parseLong s)
    (catch NumberFormatException _ nil)))

(defn- parse-sem [s]
  (case s "2" :spring "6" :summer "9" :fall))

(def ^:private day-res {:monday #"M" :tuesday #"T(?!H)"
                        :wednesday #"W" :thursday #"TH" :friday #"F"})

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
     :semester (parse-sem sem)
     :dept-abbrev dept-abbr
     :dept-name dept-name
     :course-num course-num
     :topic (Long/parseLong topic)
     :uuid uuid
     :section-num sect-num
     :title title
     :instructor-name inst
     :instructor-eid inst-eid
     :instructor-email inst-email
     :meeting-days (parse-days days)
     :start-time from
     :end-time to
     :building bldg
     :room room
     :max-enrollment (Long/parseLong max-enrl)
     :seats-taken (Long/parseLong seats-taken)
     :cross-list-count (long-or-nil xlist-ct)
     :cross-list-ptr (long-or-nil xlist-ptr)
     :cross-lists (parse-xlists xlists)}))

(defn -main []
  (let [data (with-open [f (clojure.java.io/reader "Current_Semester_Report.txt")]
               (->> f
                    (line-seq)
                    (drop 33)                                        ; todo unhardcode
                    (map parse)
                    (doall)))]
    (println "Parsed" (count data) "classes from text file")
    (clojure.spec/explain :clojure-scratchpad.scraper.validation/classes data)
    (for [c data :when (.contains (:instructor-name c) "GHEITH")]
      c)))