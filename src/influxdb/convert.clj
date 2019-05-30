(ns influxdb.convert
  "For convenience a point can be represented by a hash-map:
     {:measurement \"cpu\"
      :tags {:host \"serverA\" :region \"us_west\"}
      :fields {:value 0.64 :count 3}
      :time 1434067467000000000}

  Just like for inserts and using the Line Protocol only measurement and at
  least one field is mandatory:
  https://docs.influxdata.com/influxdb/v1.7/write_protocols/line_protocol_reference/"
  (:require [clojure.string :as str]
            [influxdb.precision :as precision]))

(defn format-data-type
  "See https://docs.influxdata.com/influxdb/v1.7/write_protocols/line_protocol_reference#data-types"
  [v]
  (cond
    (float? v) v
    (boolean? v) (if v "t" "f")
    (int? v) (str v "i")
    :else (str "\"" (str/escape v {\" "\\\""}) "\"")))

(defn escape
  "See https://docs.influxdata.com/influxdb/v1.7/write_protocols/line_protocol_reference#special-characters"
  [s]
  (str/escape s {\, "\\,"
                 \= "\\="
                 \space "\\ "}))

(defn key-val->str
  [val->str m]
  (map #(str (escape (name (key %)))
             "="
             (val->str (val %))) m))

(defmulti ->nano
  "Takes an instant and returns it as nano seconds since epoch."
  (fn [inst] (type inst)))

(defmethod ->nano :default
  [inst]
  (identity inst))

(defn adjust-precision
  "Takes an instant representation and returns the adjusted instant according to
  the precision. Use nil as precision to leave the instant as-is i.e. when
  already represented in the correct precision."
  [inst precision]
  (if-let [ratio (get precision/ratios precision)]
    (long (/ (->nano inst) ratio))
    inst))

(defn point->line
  "Takes a point (hash-map) and optionally a precision and returns a string in
  the Line Protocol syntax."
  ([point]
   (point->line point ::precision/ns))
  ([{:keys [measurement fields tags time] :as point} precision]
   (str (str/join "," (conj (key-val->str escape tags)
                            (str/escape measurement {\, "\\," \space "\\ "})))
        " "
        (str/join "," (key-val->str format-data-type fields))
        (when time
          (str " " (adjust-precision time precision))))))
