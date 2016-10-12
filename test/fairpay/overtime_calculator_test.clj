(ns fairpay.overtime-calculator-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [fairpay.calculators.wage-calculator :refer :all]))


(def all-params {:minimum-wage "7.25" :hours-worked "40" :gross-wages "290"})

(def missing-minwage {:hours-worked "40" :gross-wages "290"})
(def missing-hours-worked {:minimum-wage "7.25" :gross-wages "290"})
(def missing-gross-wages {:minimum-wage "7.25" :hours-worked "40"})
(def missing-multiple {:minimum-wage "7.25"})

(def not-numeric-minwage {:minimum-wage "abc" :hours-worked "40" :gross-wages "290"})
(def not-numeric-hours-worked {:minimum-wage  "7.25" :hours-worked "abc" :gross-wages "290"})
(def not-numeric-gross-wages {:minimum-wage "7.25" :hours-worked "40" :gross-wages "abc"})
(def not-numeric-multiple {:minimum-wage "abc" :hours-worked "abc" :gross-wages "abc"})

(def empty-string-minwage {:minimum-wage "" :hours-worked "40" :gross-wages "290"})
(def nil-minwage {:minimum-wage nil :hours-worked "40" :gross-wages "290"})

;;These tests are to check for missing or invalid data
(deftest missing-minwage-test
  (let [response (calculate-weekly-pay missing-minwage)]
    (is (= response {:is-error true 
                     :error {:message "The following fields are not present or are invalid" :fields ["minimum-wage"]}}))))

(deftest missing-hours-worked-test
  (let [response (calculate-weekly-pay missing-hours-worked)]
    (is (= response {:is-error true 
                     :error {:message "The following fields are not present or are invalid" :fields ["hours-worked"]}}))))

(deftest missing-gross-wages-test
  (let [response (calculate-weekly-pay missing-gross-wages)]
    (is (= response {:is-error true 
                     :error {:message "The following fields are not present or are invalid" :fields ["gross-wages"]}}))))

(deftest missing-multiple-test
  (let [response (calculate-weekly-pay missing-multiple)]
    (is (= response {:is-error true 
                     :error {:message "The following fields are not present or are invalid" :fields ["hours-worked" "gross-wages"]}}))))

(deftest not-numeric-minwage-test
  (let [response (calculate-weekly-pay not-numeric-minwage)]
    (is (= response {:is-error true 
                     :error {:message "The following fields are not present or are invalid" :fields ["minimum-wage"]}}))))

(deftest not-numeric-hours-worked-test
  (let [response (calculate-weekly-pay not-numeric-hours-worked)]
    (is (= response {:is-error true 
                     :error {:message "The following fields are not present or are invalid" :fields ["hours-worked"]}}))))

(deftest not-numeric-gross-wages-test
  (let [response (calculate-weekly-pay not-numeric-gross-wages)]
    (is (= response {:is-error true 
                     :error {:message "The following fields are not present or are invalid" :fields ["gross-wages"]}}))))

(deftest not-numeric-multiple-test
  (let [response (calculate-weekly-pay not-numeric-multiple)]
    (is (= response {:is-error true 
                     :error {:message "The following fields are not present or are invalid" :fields ["minimum-wage" "hours-worked" "gross-wages"]}}))))

(deftest empty-string-minwage-test
  (let [response (calculate-weekly-pay empty-string-minwage)]
    (is (= response {:is-error true 
                     :error {:message "The following fields are not present or are invalid" :fields ["minimum-wage"]}}))))

(deftest nil-minwage-test
  (let [response (calculate-weekly-pay nil-minwage)]
    (is (= response {:is-error true
                     :error {:message "The following fields are not present or are invalid" :fields ["minimum-wage"]}}))))

;;Testing different scenarios with valid data
(def full-hours-proper-paid {:minimum-wage "7.25" :hours-worked "40" :gross-wages "290"})
(def full-hours-under-paid {:minimum-wage "7.25" :hours-worked "40" :gross-wages "200"})
(def full-hours-over-paid {:minimum-wage "7.25" :hours-worked "40" :gross-wages "400"})

(deftest full-hours-proper-paid-test
  (let [response (calculate-weekly-pay full-hours-proper-paid)]
    (is (= response {:is-error false 
                     :error {}
                     :fairpay true 
                     :wage-breakdown {:normal-hours "40" 
                                      :normal-pay "290.00" 
                                      :overtime-hours "0" 
                                      :overtime-pay "0.00" 
                                      :total-pay "290.00"
                                      :reported-gross-wages "290.00"
                                      :difference "0.00"}}))))

(deftest full-hours-under-paid-test
  (let [response (calculate-weekly-pay full-hours-under-paid)]
    (is (= response {:is-error false 
                     :error {}
                     :fairpay false 
                     :wage-breakdown {:normal-hours "40" 
                                      :normal-pay "290.00" 
                                      :overtime-hours "0" 
                                      :overtime-pay "0.00" 
                                      :total-pay "290.00"
                                      :reported-gross-wages "200.00"
                                      :difference "90.00"}}))))

(deftest full-hours-over-paid-test
  (let [response (calculate-weekly-pay full-hours-over-paid)]
    (is (= response {:is-error false 
                     :error {}
                     :fairpay true
                     :wage-breakdown {:normal-hours "40" 
                                      :normal-pay "290.00" 
                                      :overtime-hours "0" 
                                      :overtime-pay "0.00" 
                                      :total-pay "290.00"
                                      :reported-gross-wages "400.00"
                                      :difference "-110.00"}}))))

(def under-hours-proper-paid {:minimum-wage "7.25" :hours-worked "30" :gross-wages "217.5"})
(def under-hours-under-paid {:minimum-wage "7.25" :hours-worked "30" :gross-wages "200"})
(def under-hours-over-paid {:minimum-wage "7.25" :hours-worked "30" :gross-wages "250"})

(deftest under-hours-proper-paid-test
  (let [response (calculate-weekly-pay under-hours-proper-paid)]
    (is (= response {:is-error false 
                     :error {}
                     :fairpay true 
                     :wage-breakdown {:normal-hours "30" 
                                      :normal-pay "217.50" 
                                      :overtime-hours "0" 
                                      :overtime-pay "0.00" 
                                      :total-pay "217.50"
                                      :reported-gross-wages "217.50"
                                      :difference "0.00"}}))))

(deftest under-hours-under-paid-test
  (let [response (calculate-weekly-pay under-hours-under-paid)]
    (is (= response {:is-error false 
                     :error {}
                     :fairpay false 
                     :wage-breakdown {:normal-hours "30" 
                                      :normal-pay "217.50" 
                                      :overtime-hours "0" 
                                      :overtime-pay "0.00" 
                                      :total-pay "217.50"
                                      :reported-gross-wages "200.00"
                                      :difference "17.50"}}))))

(deftest under-hours-over-paid-test
  (let [response (calculate-weekly-pay under-hours-over-paid)]
    (is (= response {:is-error false 
                     :error {}
                     :fairpay true
                     :wage-breakdown {:normal-hours "30" 
                                      :normal-pay "217.50" 
                                      :overtime-hours "0" 
                                      :overtime-pay "0.00" 
                                      :total-pay "217.50"
                                      :reported-gross-wages "250.00"
                                      :difference "-32.50"}}))))

(def overtime-hours-proper-paid {:minimum-wage "7.25" :hours-worked "50" :gross-wages "398.75"})
(def overtime-hours-under-paid {:minimum-wage "7.25" :hours-worked "50" :gross-wages "300"})
(def overtime-hours-over-paid {:minimum-wage "7.25" :hours-worked "50" :gross-wages "450"})


(deftest overtime-hours-proper-paid-test
  (let [response (calculate-weekly-pay overtime-hours-proper-paid)]
    (is (= response {:is-error false 
                     :error {}
                     :fairpay true 
                     :wage-breakdown {:normal-hours "40" 
                                      :normal-pay "290.00" 
                                      :overtime-hours "10" 
                                      :overtime-pay "108.75" 
                                      :total-pay "398.75"
                                      :reported-gross-wages "398.75"
                                      :difference "0.00"}}))))

(deftest overtime-hours-under-paid-test
  (let [response (calculate-weekly-pay overtime-hours-under-paid)]
    (is (= response {:is-error false 
                     :error {}
                     :fairpay false 
                     :wage-breakdown {:normal-hours "40" 
                                      :normal-pay "290.00" 
                                      :overtime-hours "10" 
                                      :overtime-pay "108.75" 
                                      :total-pay "398.75"
                                      :reported-gross-wages "300.00"
                                      :difference "98.75"}}))))

(deftest overtime-hours-over-paid-test
  (let [response (calculate-weekly-pay overtime-hours-over-paid)]
    (is (= response {:is-error false 
                     :error {}
                     :fairpay true 
                     :wage-breakdown {:normal-hours "40" 
                                      :normal-pay "290.00" 
                                      :overtime-hours "10" 
                                      :overtime-pay "108.75" 
                                      :total-pay "398.75"
                                      :reported-gross-wages "450.00"
                                      :difference "-51.25"}}))))
