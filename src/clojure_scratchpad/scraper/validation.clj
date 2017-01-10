(ns clojure-scratchpad.scraper.validation
  (:require [clojure.spec :as s]))

(s/def ::year nat-int?)

(s/def ::semester #{:spring :summer :fall})

(s/def ::dept-abbrev (s/and string? #(re-matches #"[A-Z ]{3}" %)))

(s/def ::dept-name string?)

(s/def ::course-num (s/and string? #(re-matches #"\d{3,}[A-Z]?" %)))

(s/def ::topic nat-int?)

(s/def ::uuid (s/and string? #(re-matches #"\d{5}" %)))

(s/def ::section-num (s/and string? #(re-matches #"\d{6}" %)))

(s/def ::title string?)

(s/def ::instructor-name string?)

(s/def ::instructor-eid string?)

(s/def ::instructor-email string?)

(s/def ::weekday #{:monday :tuesday :wednesday :thursday :friday})
(s/def ::meeting-days (s/and set? (s/coll-of ::weekday)))

(s/def ::military-time (s/and string?
                          #(re-matches #"\d{4}" %)
                          #(<= 0 (Long/valueOf (subs % 0 2)) 23)
                          #(<= 0 (Long/valueOf (subs % 2 4)) 59)))
(s/def ::start-time (s/nilable ::military-time))
(s/def ::end-time (s/nilable ::military-time))

(s/def ::building (s/and string? #(re-matches #"\w{3}" %)))

(s/def ::room string?)

(s/def ::max-enrollment nat-int?)

(s/def ::seats-taken nat-int?)

(s/def ::cross-list-count (s/nilable nat-int?))

(s/def ::cross-list-ptr (s/nilable ::uuid))

(s/def ::cross-lists (s/coll-of ::uuid))

(s/def ::class (s/keys :req-un [::year ::semester ::dept-abbrev ::dept-name ::course-num
                                ::topic ::uuid ::section-num ::title ::instructor-name
                                ::instructor-eid ::instructor-email ::meeting-days
                                ::start-time ::end-time ::building ::room ::max-enrollment
                                ::seats-taken ::cross-list-count ::cross-list-ptr ::cross-lists]))

(s/def ::classes (s/coll-of ::class))