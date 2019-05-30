(defproject fullspectrum/influxdb-client "1.0.0-SNAPSHOT"
  :description "Clojure InfluxDB client. Supporting InfluxDB 1.7 and without
  introducing an unnecessary extra dependency like the Java client."
  :url "https://github.com/full-spectrum/clj-influxdb-client/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[clj-http "3.9.1"]
                 [metosin/jsonista "0.2.2"]]
  :profiles {:dev {:dependencies [[clojure.java-time "0.3.2"]
                                  [org.clojure/clojure "1.10.0"]]}})
