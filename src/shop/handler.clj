(ns shop.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer [resource-response response]]
            [ring.middleware.json :as middleware]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
            [immutant.web :as web]
            [shop.db-access :as db]
            [shop.logistics-service :as logistics]
            [shop.utilities :as utils]
            ))


; -------
; REST Response creation (in JSON)
; -------

(defn create-error-response
  "creates a HTTP response for an error"
  [error-code error-message]
  {:status error-code
   :body {:message error-message} } )

(defn create-success-message
  "creates a HTTP response for success"
  [success-message price]
  {:status 201
   :body {:message success-message :price-per-item price} })


; -------
; DB Response Handling
; -------

(defn successful?
  "checks if the response is successful, or not"
  [resp]
  (= 201 (get resp :status)))

(defn update-response
  ""
  [resp invoice desired-amount]
  (let [price-per-item (get (get resp :body) :price-per-item)
        total-cost (str (* desired-amount price-per-item))
        updated-invoice (clojure.string/replace invoice "__cost__" total-cost)
        updated-body (assoc (get resp :body) :invoice updated-invoice)
        update-resp (assoc resp :body updated-body)]
    update-resp))


; -------
; DB access and Data retrieval
; -------

(defn retrieve-books-by-title
  "retrieves a book from the DB, searching it by its title"
  [book-title desired-amount]
  (let [price (db/get-books-by-title book-title desired-amount)]
    (if (< price 0)
      (create-error-response 601 "Your request cannot be satisfied (not enough books in stock?)")
      (create-success-message "Your request succeeded" price))))

(defn retrieve-books-by-isbn
  "retrieves a book from the DB, searching it by its isbn"
  [book-isbn desired-amount]
  (let [price (db/get-books-by-isbn book-isbn desired-amount)]
    (if (< price 0)
      (create-error-response 601 "Your request cannot be satisfied (not enough books in stock?)")
      (create-success-message "Your request succeeded" price))))

(defn retrieve-books
  "retrieves a book from the DB"
  [book-title book-isbn desired-amount]
  (do
    (utils/sleep 1)
    (if (nil? book-isbn)
      (retrieve-books-by-title book-title desired-amount)
      (retrieve-books-by-isbn book-isbn desired-amount))))


; -------
; Basic Request handling
; -------

(defn process-request
  "processes a valid request"
  [book-title book-isbn desired-amount customer-name]
  (do (def invoice (future (logistics/invoice-service customer-name)))
      (def resp (retrieve-books book-title book-isbn desired-amount))
      (if (successful? resp)
        (update-response resp @invoice desired-amount)
        resp)))

(defn handle-request
  "handles the REST request"
  [request]
  (let [{ {book-title :title
           book-isbn :isbn
           desired-amount-raw :amount
           customer-name :customer} :params} request
        desired-amount (utils/convert-to-integer desired-amount-raw)]
    (cond
      (and (nil? book-title) (nil? book-isbn))
        (create-error-response 400 "Book title or book isbn should be provided")
      (and (not (nil? book-title)) (not (nil? book-isbn)))
        (create-error-response 400 "Only one of book title, book isbn should be provided")
      (nil? desired-amount)
        (create-error-response 400 "Amount should be provided")
      (< desired-amount 1)
        (create-error-response 400 "Amount should be a positive number")
      (nil? customer-name)
        (create-error-response 400 "Customer should be provided")
      :else
        (process-request book-title book-isbn desired-amount customer-name)
      )))


; -------
; REST infrastructure
; -------

(defroutes app-routes
  ; http://localhost:8080/
  (GET "/" [] "Hello World")

  ; http://localhost:8080/book?title=cooking&amount=2
  (GET "/book" request (handle-request request))

  ; others
  (route/not-found "Not Found"))

; Define the Application
(def app
  (-> app-routes
      ; {:keywords? true} when the body is parsed makes the keys return as keywords
      ; and not as strings
      (middleware/wrap-json-body {:keywords? true})
      (middleware/wrap-json-response)
      (wrap-defaults api-defaults)
  ))

(defn start
  "starts the web application"
  []
  (web/run app))

