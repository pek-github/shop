(ns shop.data
  (:require [clojure.java.jdbc :as sql]
            [java-jdbc.ddl :as ddl]
  ))

; -------
; DB and Table handling
; -------

; specifications for the H2 DB connection
(defonce db-specs
  {:classname "org.h2.Driver" ; must be in classpath
   :subprotocol "h2"
   :subname "tcp://localhost/~/test"
   :user     "sa"
   :password ""})

(defn get-db-specs [] (db-specs))

(defn single-quote [text]
  (str "'" text "'"))

(defn table-exists?
  "checks if a table exists in the DB"
  [table-schema table-name]
  (let [query (str "SELECT (COUNT(*) > 0) AS found "
               "FROM INFORMATION_SCHEMA.TABLES "
               "WHERE (TABLE_SCHEMA = "
               (single-quote table-schema)
               " ) AND "
               "(TABLE_NAME = "
               (single-quote table-name)
               " ); "
               )]
    (let [resp (sql/query db-specs [query])]
      (get (first resp) :found))))

(defn create-table
  "creates the book-table in the DB"
  [books-table]
  (sql/db-do-commands db-specs
    (sql/create-table-ddl books-table
      [:book_id :int :primary :key ]
      [:title "varchar(100)"]
      [:isbn "varchar(32)"]
      [:author "varchar(100)"]
      [:edition "integer"]
      [:items "integer"]
      [:price "decimal(18,2)"]
    )))

(defn table-populated?
  "checks if a table is populated in the DB"
  [table-name]
  (let [query (str "SELECT (COUNT(*) > 0) AS populated "
                   "FROM " table-name " ;"
                   )]
    (let [resp (sql/query db-specs [query])]
      (get (first resp) :populated))))

(defn populate-table
  "Populate the given table books with data"
  [table-name data]
  (doseq [d data]
    (sql/insert! db-specs table-name d)))


; -------
; Data
; -------

; define some books
(def b1
  {:book_id 10 :title "maths" :isbn "1234-5678-90" :author "Ada Lovelace" :edition 1 :items 100 :price 120.5})
(def b2
  {:book_id 20 :title "science" :isbn "1904-8657-11" :author "Einstein" :edition 2 :items 150 :price 234.5})
(def b3
  {:book_id 30 :title "cooking" :isbn "9823-4632-14" :author "Chef Apic" :edition 4 :items 300 :price 99.0})
(def b4
  {:book_id 40 :title "ballet" :isbn "3628-4882-32" :author "Maria" :edition 2 :items 200 :price 78.2})

(def available-books (list b1 b2 b3 b4))


; -------
; Table and Data handling
; -------

(defn handle-table
  "if necessary, it creates the given table of the given schema"
  [table-schema table-name]
  (if (= false (table-exists? table-schema table-name))
    (create-table table-name)))

(defn handle-data
  "if necessary, it populates the given table with the given data"
  [table-name table-data]
  (if (= false (table-populated? table-name))
    (populate-table table-name table-data)))

(defn handle-table-and-data
  "handles everything related to the given table (of the given schema)
   and the given data"
  [table-schema table-name table-data]
  (do
    (handle-table table-schema table-name)
    (handle-data table-name table-data)))


; -------
; Invoke table and data logic
; -------
(def book-table-schema "PUBLIC")
(def book-table-name "BOOKS")

(handle-table-and-data book-table-schema book-table-name available-books)

