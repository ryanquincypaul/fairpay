(ns fairpay.handler
  (:use [markdown.core]) 
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as middleware]
            [ring.util.response :refer [response]]
            [fairpay.calculators.wage-calculator  :refer :all]
            [clojure.java.io :as io]))

(def api-reference-md-resource "public/fairpay-api-reference.md")

(defn load-api-reference
  []
  (md-to-html-string (slurp (io/resource api-reference-md-resource))))

(defroutes app-routes
  (GET "/" [] (load-api-reference))
  (GET "/calculate-weekly-pay" {params :params} (response (calculate-weekly-pay params)) )
  (route/not-found "Not Found"))

(def app
  (-> (wrap-defaults app-routes site-defaults)
      (middleware/wrap-json-body {:keywords? true})
      (middleware/wrap-json-response app-routes)))
