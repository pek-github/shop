(defproject shop "0.1.0-SNAPSHOT"
  :description "This is a small online shop (bookshop) with a few books for sale"
  :url "http://example.com/NONE"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [compojure "1.4.0"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [org.immutant/web "2.1.0"]
                 [com.h2database/h2 "1.3.170"]
                 [org.clojure/java.jdbc "0.4.1"]
                 [java-jdbc/dsl "0.1.0"]
                 ]
  :plugins [[lein-ring "0.9.7"]
            [lein-immutant "2.0.0"]
            ]
  :ring { :handler shop.handler/app }
  :main shop.handler/start
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
