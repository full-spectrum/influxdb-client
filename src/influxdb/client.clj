(ns influxdb.client
  "When interacting with the InfluxDB HTTP API a \"connection\" needs to be
  provided. A connection is a hash-map describing how to connect to InfluxDB and
  could look like the following:
    {:url \"http://localhost:8086\"
     :username \"root\"
     :password \"root\"}
  Only the :url is mandatory."
  (:require [clj-http.client :as http-client]
            [clojure.string :as str]
            [jsonista.core :as json]))

(defn prep-query-params
  "Constructs query params automatically including authentication from
  connection if present."
  [{:keys [username password] :as conn} base-params & overwrite-params]
  (let [auth-params (when (and username password)
                      {"u" username "p" password})]
    (apply merge (conj overwrite-params base-params auth-params))))

(def available-methods
  {::read http-client/get
   ::manage http-client/post})

(defn query
  "Takes a connection along with query method and the actual query which can be
  either a string (single query) or a list/vector of strings (multiple queries).
  Optionally a fourth argument containing query params can be provided. For a
  list of valid query params see:
  https://docs.influxdata.com/influxdb/v1.7/tools/api/#query-string-parameters-1"
  ([conn method q]
   (query conn method q {}))
  ([conn method q query-params]
   (let [request-fn (or (method available-methods)
                        (throw (ex-info "Unknown query method." {:method method})))]
     (request-fn
      (str (:url conn) "/query")
      {:query-params (prep-query-params conn query-params
                                        {"q" (if (string? q) q (str/join ";" q))})}))))

(defn write
  "For valid query params see: https://docs.influxdata.com/influxdb/v1.7/tools/api/#query-string-parameters-2"
  ([conn db data]
   (write conn db data {}))
  ([conn db data query-params]
   (http-client/post
      (str (:url conn) "/write")
      {:content-type :x-www-form-urlencoded
       :body data
       :query-params (prep-query-params conn query-params {"db" db})})))

(defn unwrap
  "Takes a http response from the API endpoint and converts it to a Clojure data
  structure for convenience."
  [response]
  (-> response
      :body
      (json/read-value)
      (get "results")))
