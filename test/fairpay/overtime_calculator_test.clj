(ns fairpay.overtime-calculator-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [fairpay.calculators.wage-calculator :refer :all]))


(def all-params {:minimum_wage "7.25" :hours_worked "40" :gross_wages "290"})

(def missing-minwage {:hours_worked "40" :gross_wages "290"})
(def missing-hours-worked {:minimum_wage "7.25" :gross_wages "290"})
(def missing-gross-wages {:minimum_wage "7.25" :hours_worked "40"})
(def missing-multiple {:minimum_wage "7.25"})

(def not-numeric-minwage {:minimum_wage "abc" :hours_worked "40" :gross_wages "290"})
(def not-numeric-hours-worked {:minimum_wage  "7.25" :hours_worked "abc" :gross_wages "290"})
(def not-numeric-gross-wages {:minimum_wage "7.25" :hours_worked "40" :gross_wages "abc"})
(def not-numeric-multiple {:minimum_wage "abc" :hours_worked "abc" :gross_wages "abc"})

(def empty-string-minwage {:minimum_wage "" :hours_worked "40" :gross_wages "290"})
(def nil-minwage {:minimum_wage nil :hours_worked "40" :gross_wages "290"})

;;These tests are to check for missing or invalid data
(deftest missing-minwage-test
  (let [response (calculate-weekly-pay missing-minwage)]
    (is (= response {:is_error true 
                     :error {:message "The following fields are not present or are invalid" :fields ["minimum_wage"]}}))))

(deftest missing-hours-worked-test
  (let [response (calculate-weekly-pay missing-hours-worked)]
    (is (= response {:is_error true 
                     :error {:message "The following fields are not present or are invalid" :fields ["hours_worked"]}}))))

(deftest missing-gross-wages-test
  (let [response (calculate-weekly-pay missing-gross-wages)]
    (is (= response {:is_error true 
                     :error {:message "The following fields are not present or are invalid" :fields ["gross_wages"]}}))))

(deftest missing-multiple-test
  (let [response (calculate-weekly-pay missing-multiple)]
    (is (= response {:is_error true 
                     :error {:message "The following fields are not present or are invalid" :fields ["hours_worked" "gross_wages"]}}))))

(deftest not-numeric-minwage-test
  (let [response (calculate-weekly-pay not-numeric-minwage)]
    (is (= response {:is_error true 
                     :error {:message "The following fields are not present or are invalid" :fields ["minimum_wage"]}}))))

(deftest not-numeric-hours-worked-test
  (let [response (calculate-weekly-pay not-numeric-hours-worked)]
    (is (= response {:is_error true 
                     :error {:message "The following fields are not present or are invalid" :fields ["hours_worked"]}}))))

(deftest not-numeric-gross-wages-test
  (let [response (calculate-weekly-pay not-numeric-gross-wages)]
    (is (= response {:is_error true 
                     :error {:message "The following fields are not present or are invalid" :fields ["gross_wages"]}}))))

(deftest not-numeric-multiple-test
  (let [response (calculate-weekly-pay not-numeric-multiple)]
    (is (= response {:is_error true 
                     :error {:message "The following fields are not present or are invalid" :fields ["minimum_wage" "hours_worked" "gross_wages"]}}))))

(deftest empty-string-minwage-test
  (let [response (calculate-weekly-pay empty-string-minwage)]
    (is (= response {:is_error true 
                     :error {:message "The following fields are not present or are invalid" :fields ["minimum_wage"]}}))))

(deftest nil-minwage-test
  (let [response (calculate-weekly-pay nil-minwage)]
    (is (= response {:is_error true
                     :error {:message "The following fields are not present or are invalid" :fields ["minimum_wage"]}}))))

;;Testing different scenarios with valid data
(def full-hours-proper-paid {:minimum_wage "7.25" :hours_worked "40" :gross_wages "290"})
(def full-hours-under-paid {:minimum_wage "7.25" :hours_worked "40" :gross_wages "200"})
(def full-hours-over-paid {:minimum_wage "7.25" :hours_worked "40" :gross_wages "400"})

(deftest full-hours-proper-paid-test
  (let [response (calculate-weekly-pay full-hours-proper-paid)]
    (is (= response {:is_error false 
                     :error {}
                     :fairpay true 
                     :wage_breakdown {:normal_hours "40" 
                                      :normal_pay "290.00" 
                                      :overtime_hours "0" 
                                      :overtime_pay "0.00" 
                                      :total_pay "290.00"
                                      :reported_gross_wages "290.00"
                                      :difference "0.00"}}))))

(deftest full-hours-under-paid-test
  (let [response (calculate-weekly-pay full-hours-under-paid)]
    (is (= response {:is_error false 
                     :error {}
                     :fairpay false 
                     :wage_breakdown {:normal_hours "40" 
                                      :normal_pay "290.00" 
                                      :overtime_hours "0" 
                                      :overtime_pay "0.00" 
                                      :total_pay "290.00"
                                      :reported_gross_wages "200.00"
                                      :difference "90.00"}}))))

(deftest full-hours-over-paid-test
  (let [response (calculate-weekly-pay full-hours-over-paid)]
    (is (= response {:is_error false 
                     :error {}
                     :fairpay true
                     :wage_breakdown {:normal_hours "40" 
                                      :normal_pay "290.00" 
                                      :overtime_hours "0" 
                                      :overtime_pay "0.00" 
                                      :total_pay "290.00"
                                      :reported_gross_wages "400.00"
                                      :difference "-110.00"}}))))

(def under-hours-proper-paid {:minimum_wage "7.25" :hours_worked "30" :gross_wages "217.5"})
(def under-hours-under-paid {:minimum_wage "7.25" :hours_worked "30" :gross_wages "200"})
(def under-hours-over-paid {:minimum_wage "7.25" :hours_worked "30" :gross_wages "250"})

(deftest under-hours-proper-paid-test
  (let [response (calculate-weekly-pay under-hours-proper-paid)]
    (is (= response {:is_error false 
                     :error {}
                     :fairpay true 
                     :wage_breakdown {:normal_hours "30" 
                                      :normal_pay "217.50" 
                                      :overtime_hours "0" 
                                      :overtime_pay "0.00" 
                                      :total_pay "217.50"
                                      :reported_gross_wages "217.50"
                                      :difference "0.00"}}))))

(deftest under-hours-under-paid-test
  (let [response (calculate-weekly-pay under-hours-under-paid)]
    (is (= response {:is_error false 
                     :error {}
                     :fairpay false 
                     :wage_breakdown {:normal_hours "30" 
                                      :normal_pay "217.50" 
                                      :overtime_hours "0" 
                                      :overtime_pay "0.00" 
                                      :total_pay "217.50"
                                      :reported_gross_wages "200.00"
                                      :difference "17.50"}}))))

(deftest under-hours-over-paid-test
  (let [response (calculate-weekly-pay under-hours-over-paid)]
    (is (= response {:is_error false 
                     :error {}
                     :fairpay true
                     :wage_breakdown {:normal_hours "30" 
                                      :normal_pay "217.50" 
                                      :overtime_hours "0" 
                                      :overtime_pay "0.00" 
                                      :total_pay "217.50"
                                      :reported_gross_wages "250.00"
                                      :difference "-32.50"}}))))

(def overtime-hours-proper-paid {:minimum_wage "7.25" :hours_worked "50" :gross_wages "398.75"})
(def overtime-hours-under-paid {:minimum_wage "7.25" :hours_worked "50" :gross_wages "300"})
(def overtime-hours-over-paid {:minimum_wage "7.25" :hours_worked "50" :gross_wages "450"})


(deftest overtime-hours-proper-paid-test
  (let [response (calculate-weekly-pay overtime-hours-proper-paid)]
    (is (= response {:is_error false 
                     :error {}
                     :fairpay true 
                     :wage_breakdown {:normal_hours "40" 
                                      :normal_pay "290.00" 
                                      :overtime_hours "10" 
                                      :overtime_pay "108.75" 
                                      :total_pay "398.75"
                                      :reported_gross_wages "398.75"
                                      :difference "0.00"}}))))

(deftest overtime-hours-under-paid-test
  (let [response (calculate-weekly-pay overtime-hours-under-paid)]
    (is (= response {:is_error false 
                     :error {}
                     :fairpay false 
                     :wage_breakdown {:normal_hours "40" 
                                      :normal_pay "290.00" 
                                      :overtime_hours "10" 
                                      :overtime_pay "108.75" 
                                      :total_pay "398.75"
                                      :reported_gross_wages "300.00"
                                      :difference "98.75"}}))))

(deftest overtime-hours-over-paid-test
  (let [response (calculate-weekly-pay overtime-hours-over-paid)]
    (is (= response {:is_error false 
                     :error {}
                     :fairpay true 
                     :wage_breakdown {:normal_hours "40" 
                                      :normal_pay "290.00" 
                                      :overtime_hours "10" 
                                      :overtime_pay "108.75" 
                                      :total_pay "398.75"
                                      :reported_gross_wages "450.00"
                                      :difference "-51.25"}}))))
