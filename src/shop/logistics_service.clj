(ns shop.logistics-service
  (:require [shop.utilities :as utils]))

(defonce my-shop "BookShop Inc.")

(defn create-invoice-template
  "creates the template for the invoice"
  [customer timestamp]
  (str "Enterprise " my-shop
       " acknowledges the legal transaction of "
       "accepting __cost__ $ for books, from customer "
       customer ". "
       "Transaction took place on " timestamp "."
       ))

(defn invoice-service
  "a simulated call to an invoice service whose
   purpose is to create an invoice for the customer"
  [customer]
  (let [now (java.util.Date.)]
    (utils/sleep-random-millisecs 2000 3000)
    (create-invoice-template customer now)))

