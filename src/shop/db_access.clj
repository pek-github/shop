(ns shop.db-access
  (:require [clojure.java.jdbc :as sql]
            [shop.data :as data]))

; -------
; Use Case: Buy a book by title
; -------

(defn find-book-by-title
  "find a book, identified by its title, a number of times"
  [book-title desired-quantity]
  (sql/query
    data/db-specs
    ["SELECT items, price FROM books WHERE title = ? AND items > ?"
     book-title desired-quantity
     ]))

(defn update-book-quantity-by-title
  "update the available quantity of a given book"
  [book-title new-quantity]
  (sql/update! data/db-specs :books {:items new-quantity} ["title = ?" book-title]))

(defn get-books-by-title
  "retrieve a book, identified by its title, a number of times"
  [book-title desired-quantity]
  (let [response (find-book-by-title book-title desired-quantity)]
    (if (empty? response)
      -1
      (let [available (get (first response) :items)
            price (get (first response) :price)
            remaining (- available desired-quantity)]
        (do
          (update-book-quantity-by-title book-title remaining)
          price)))))


; -------
; Use Case: Buy a book by isbn
; -------

(defn find-book-by-isbn
  "find a book, identified by its isbn, a number of times"
  [book-isbn desired-quantity]
  (sql/query
    data/db-specs
    ["SELECT items, price FROM books WHERE isbn = ? AND items > ?"
     book-isbn desired-quantity
     ]))

(defn update-book-quantity-by-isbn
  "update the available quantity of a given book"
  [book-isbn new-quantity]
  (sql/update! data/db-specs :books {:items new-quantity} ["isbn = ?" book-isbn]))

(defn get-books-by-isbn
  "retrieve a book, identified by its isbn, a number of times"
  [book-isbn desired-quantity]
  (let [response (find-book-by-isbn book-isbn desired-quantity)]
    (if (empty? response)
      -1
      (let [available (get (first response) :items)
            price (get (first response) :price)
            remaining (- available desired-quantity)]
        (do
          (update-book-quantity-by-isbn book-isbn remaining)
          price)))))

