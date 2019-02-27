(defproject dk.emcken/influxdb-client "0.1.1"
  :description "Clojure InfluxDB client"
  :url "https://github.com/jacobemcken/clj-influxdb-client/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [clj-http "3.9.1"]
                 [metosin/jsonista "0.2.2"]
                 ]
  :profiles {:dev {:dependencies [[criterium "0.4.4"]
                                  [clojure.java-time "0.3.2"]
                                  [org.clojure/clojure "1.10.0"]]}})
