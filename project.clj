(defproject fairpay "0.1.0-SNAPSHOT"
  :description "A Clojure powered web service providing fair wage calculations for low-income workers."
  :url "https://github.com/ryanquincypaul/fairpay"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [ring/ring-json "0.4.0"]
                 [markdown-clj "0.9.89"]
                 [ring-cors "0.1.8"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler fairpay.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
