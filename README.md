# InfluxDB client for Clojure

This client library communicates with the [InfluxDB HTTP API][1] (ver 1.7) and
is very small. It is still lacking a few debugging features but has the
important things for managing, reading from and writing to databases.

[1]: https://docs.influxdata.com/influxdb/v1.7/tools/api


## Installation

Add the following dependency to your `project.clj` file:

    [fullspectrum/influxdb-client "1.0.0"]

[![Clojars Project](https://img.shields.io/clojars/v/fullspectrum/influxdb-client.svg)](https://clojars.org/fullspectrum/influxdb-client)


## Usage

### Connecting

Specify how the client reaches the InfluxDB API using a hash-map:

```clojure
{:url "http://localhost:8086"}
```


Or if you have authentication also provide the username and password:

```clojure
{:url "http://localhost:8086"
 :username "root"
 :password "secret"}
```


The following code examples assumes you are in the `user` namespace and have
required the library and a connection representation (`conn`):

    user > (require '[influxdb.client :as client :refer [unwrap query write]])
    nil

    user > (def conn {:url "http://localhost:8086"})
    #'user/conn


### Read

This corresponds to `GET /query` endpoint when using the method `::client/read`.
This will work with any query that reads from the database (`SELECT` and
`SHOW`):

    user > (unwrap (query conn ::client/read "SHOW DATABASES"))
    [{"series" [{"values" [["_internal"]],
                 "columns" ["name"],
                 "name" "databases"}],
      "statement_id" 0}


### Manage

This corresponds to `POST /query` endpoint when using the method
`::client/manage`. This will work with any query that changes anything in the
database (`SELECT INTO`, `ALTER`, `CREATE`, `DELETE`, `DROP`, `GRANT`, `KILL`
and `REVOKE`):

    user > (unwrap (query conn ::client/manage "CREATE DATABASE mydb"))
    [{"statement_id" 0}]

For inserting data see the next section "Write".


### Write

If you already have the data you want to write in the [Line Protocol][2]:

    user> (:status (write conn "mydb" "mymeas,mytag=1 myfield=90"))
    204


If not you can use the `convert` namespace to generate Line Protocol syntax from
a hash-map:

    user> (require '[influxdb.convert :as convert])
    nil


The following hash-maps are all valid point representations:

```clojure
;; minimal data required by the Line Protocol
{:measurement "cpu"
 :fields {:value 0.64}}

;; now also including a few tags
{:measurement "cpu"
 :tags {:host "serverA" :region "us_west"}
 :fields {:value 0.64}}

;; now with multiple fields and different data types along with a timestamp
{:measurement "cpu"
 :fields {:value 0.64 :verified true :count 4}
 :time 1434067467000000000}
```

Use `point->line` to construct a sigle line following the Line Protocol format:

    user> (convert/point->line {:measurement "cpu" :fields {:value 0.64}})
    "cpu value=0.64"

[2]: https://docs.influxdata.com/influxdb/v1.7/write_protocols/line_protocol_reference


The following is an extreeme [example from the InfluxDB website][3] to
demonstrate escaping:

    user> (convert/point->line
           {:measurement "\"measurement with quo⚡️es and emoji\""
            :tags {"tag key with sp🚀ces" "tag,value,with\"commas\""}
            :fields {"field_k\\ey" "string field value, only \" need be esc🍭ped"}})
    "\"measurement\\ with\\ quo⚡️es\\ and\\ emoji\",tag\\ key\\ with\\ sp🚀ces=tag\\,value\\,with\"commas\" field_k\\ey=\"string field value, only \\\" need be esc🍭ped\""


[3]: https://docs.influxdata.com/influxdb/v1.7/write_protocols/line_protocol_reference#examples-2
