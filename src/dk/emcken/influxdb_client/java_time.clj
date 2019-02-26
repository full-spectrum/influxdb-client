(ns dk.emcken.influxdb-client.java-time
  (:require [dk.emcken.influxdb-client.convert :as convert]
            [java-time]))

(defmethod convert/->nano java.time.Instant
  [^java.time.Instant inst]
  (+ (* (.getEpochSecond inst) 1000000000) (.getNano inst)))
