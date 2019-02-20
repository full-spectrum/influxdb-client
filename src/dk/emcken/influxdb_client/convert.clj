(ns dk.emcken.influxdb-client.convert
  "For convenience a point can be represented by a hash-map:
     {:measurement \"cpu\"
      :tags {:host \"serverA\" :region \"us_west\"}
      :fields {:value 0.64 :count 3}
      :time 1434067467000000000}

  Just like for inserts and using the Line Protocol only measurement and at
  least one field is mandatory:
  https://docs.influxdata.com/influxdb/v1.7/write_protocols/line_protocol_reference/"
  (:require [clojure.string :as str]))

(defn val->str
  [v]
  (cond
    (float? v) v
    (boolean? v) (if v "t" "f")
    (instance? java.lang.Long v) (str v "i")
    :else (str "\"" v "\"")))

(defn key-val->str
  [m]
  (map #(str (name (key %)) "=" (val->str (val %))) m))

(def nano-seconds-pr
  {::ns 1
   ::u  1000
   ::ms 1000000
   ::s  1000000000})

;; The reason why this is implemented as a multi method is to accommodate for
;; time being provided in different formats. By default it is assumed to be an
;; integer in the correct precision, but if using objects like Joda-time or the
;; new Date Time API those are easily pluggable.
;; See the java.time.Instant below.
(defmulti adjust-time
  (fn [time _] (type time)))

(defmethod adjust-time :default
  [time precision]
  (identity time))

#_(defmethod adjust-time java.time.Instant
  [^java.time.Instant time precision]
  (let [ns (+ (* (.getEpochSecond time) 1000000000) (.getNano time))]
    (long (/ ns (precision nano-seconds-pr)))))

(defn point->line
  "Takes a point (hash-map) and optionally a precision and returns a string in
  the Line Protocol syntax."
  ([point]
   (point->line point ::ns))
  ([{:keys [measurement fields tags time] :as point} precision]
   (str (str/join "," (conj (key-val->str tags) measurement))
        " "
        (str/join "," (key-val->str fields))
        (when time
          (str " " (adjust-time time precision))))))
