(defproject fullspectrum/influxdb-client "1.0.0"
  :description "Clojure InfluxDB client. Supporting InfluxDB 1.7 and without
  introducing an unnecessary extra dependency like the Java client."
  :url "https://github.com/full-spectrum/clj-influxdb-client/"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[clj-http "3.9.1"]
                 [metosin/jsonista "0.2.2"]]
  :profiles {:dev {:dependencies [[clojure.java-time "0.3.2"]
                                  [org.clojure/clojure "1.10.0"]]}})
