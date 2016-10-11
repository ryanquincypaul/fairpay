(ns fairpay.calculators.wage-calculator)

;Looked into using a java library to do this, but then I stumbled on an SO war between using a whole library for one method or just rolling your own. I decided to just copy and paste a clojure solution from rosettacode.
(defn numeric? [s]
  (if-let [s (seq s)]
    (let [s (if (= (first s) \-) (next s) s)
          s (drop-while #(Character/isDigit %) s)
          s (if (= (first s) \.) (next s) s)
          s (drop-while #(Character/isDigit %) s)]
      (empty? s))))

(defn ^{:private true} validate-field
  "validate a field for presentness and numericness if specified"
  [field-map field-name check-if-numeric]
  (let [field-value (get field-map field-name)] 
   (if check-if-numeric
    ;check if field key exists AND if it isn't blank AND if it is numeric
    (if (or (nil? field-value) (clojure.string/blank? field-value) (not (numeric? field-value)))
     {:valid false}
     {:valid true})
    ;Don't check if it is numeric
    (if (or (nil? field-value) (clojure.string/blank? field-value))
     {:valid false}
     {:valid true}))))

(defn validate-request
  "check for presense and validity of required fields from request"
  ([request-map required-fields]
   (validate-request request-map required-fields []))
  ([request-map required-fields fields-in-error]
   (if (empty? required-fields)
    fields-in-error
    (recur request-map 
           (rest required-fields) 
           (let [validate-field-response (validate-field request-map (first required-fields) true)]
           ;If field is not valid add it to the list 
           (if (:valid validate-field-response)
             fields-in-error
             (conj fields-in-error (first required-fields))))))))

(def ^{:private true} weekly-pay-required-fields [:minimum-wage :hours-worked :gross-wages])

(defn build-invalid-fields-error-response
  "invalid-fields is a vector of strings"
  [invalid-fields]
  {:is-error true
   :error {:message "The following fields are not present or are invalid" :fields invalid-fields}})

(def ^{:private true} full-time-hours 40)
(def ^{:private true} overtime-pay-multiplier 1.5)

(defn ^{:private true} hours-worked-breakdown
  "returns hours worked split between normal and overtime hours"
  [hours-worked]
  (let [overtime-hours (- hours-worked full-time-hours)]
   (if (> overtime-hours 0)
    {:normal-hours full-time-hours :overtime-hours overtime-hours}
    {:normal-hours hours-worked :overtime-hours 0})))

(defn ^{:private true} format-money-value
  "coordinates the formatting for returned money values"
  [decimal]
  (format "%.2f" decimal))

(defn ^{:private true} form-calculate-weekly-pay-response
  "This method returns a response for the weekly pay route and assumes a validated request"
  [request]
  (let [minimum-wage (bigdec (get request :minimum-wage))
        hours-worked (bigdec (get request :hours-worked))
        reported-gross-pay (bigdec (get request :gross-wages))
        hours-breakdown (hours-worked-breakdown hours-worked)
        normal-hours (:normal-hours hours-breakdown)
        overtime-hours (:overtime-hours hours-breakdown)
        normal-pay (* normal-hours minimum-wage)
        overtime-pay (* overtime-hours minimum-wage overtime-pay-multiplier)
        total-pay (+ normal-pay overtime-pay)
        difference (- total-pay reported-gross-pay)
        fairpay (>= reported-gross-pay total-pay)]
   {:is-error false
    :error {}
    :fairpay fairpay
    :wage-breakdown {:normal-hours (str normal-hours)
                     :normal-pay (format-money-value normal-pay)
                     :overtime-hours (str overtime-hours)
                     :overtime-pay (format-money-value overtime-pay)
                     :total-pay (format-money-value total-pay)
                     :reported-gross-wages (format-money-value reported-gross-pay)
                     :difference (format-money-value difference)}}))

(defn calculate-weekly-pay 
  "Calculate minimum weekly pay given a minimum wage, hours, and how much was made in a week for comparison. The request must be a map with the required fields found in weekly-pay-required-fields as keys and numbers as values."
  [request]
  (let [invalid-fields (validate-request request weekly-pay-required-fields)]
   (if (empty? invalid-fields)
    (form-calculate-weekly-pay-response request)
    (build-invalid-fields-error-response invalid-fields))))
