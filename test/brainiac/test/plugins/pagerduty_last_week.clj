(ns brainiac.test.plugins.pagerduty-last-week
  (:import [java.text SimpleDateFormat])
  (:require [brainiac.clock :as clock])
  (:use [clojure.test]
        [clojure.contrib.mock]
        [brainiac.plugins.pagerduty-last-week]))

(defn- parse-date [s]
  (.parse (SimpleDateFormat. "yyyy-MM-dd") s))

(deftest build-url
  (testing "sets since to 7 days ago"
    (expect [clock/today (returns (parse-date "2012-02-03"))]
      (is (= "https://acme.pagerduty.com/api/v1/incidents?service=a,b,c&since=2012-01-27&fields=created_on&sort_by=created_on:asc" (url "acme" "a,b,c"))))))

(deftest test-classify-calls
  (testing "middle of night is a wake up"
    (is (= "wake-up" (classify-calls [1 1]))))
  (testing "dinner time is a bother"
    (is (= "outside-business-hours" (classify-calls [20 20])))) 
  (testing "noon is none"
    (is (= "none" (classify-calls [12 12])))))

(def example-json (java.io.ByteArrayInputStream. (.getBytes (slurp "test/brainiac/test/data/pagerduty_last_week.json"))))

(deftest test-transform
  (let [result (transform example-json)]
    (testing "sets name"
      (is (= "pagerduty-last-week" (:name result))))
    (testing "sets type"
      (is (= "week-calendar" (:type result))))
    (testing "sets data"
      (is (= {:date "Feb 3" :count 5 :impact "wake-up"} (first (:data result)))))))
