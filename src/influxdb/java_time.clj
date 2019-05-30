(ns influxdb.java-time
  (:require [influxdb.convert :as convert]
            [java-time]))

(defmethod convert/->nano java.time.Instant
  [^java.time.Instant inst]
  (+ (* (.getEpochSecond inst) 1000000000) (.getNano inst)))
