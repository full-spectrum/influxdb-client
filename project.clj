(defproject dk.emcken/influxdb-client "1.0.0-SNAPSHOT"
  :description "Clojure InfluxDB client"
  :url "https://github.com/jacobemcken/influxdb-client/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [clj-http "3.9.1"]
                 [metosin/jsonista "0.2.2"]
                 ]
  :profiles {:dev {:dependencies [[criterium "0.4.4"]
                                  [clojure.java-time "0.3.2"]
                                  [org.clojure/clojure "1.10.0"]]}})
