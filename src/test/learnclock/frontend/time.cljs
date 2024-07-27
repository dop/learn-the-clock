(ns learnclock.frontend.time.tests
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [learnclock.frontend.time :refer [advance-time]]))

(deftest advance
  (testing "no change"
    (is (= [10 10] (advance-time [10 10] [10 10])))
    (is (= [0 0] (advance-time [0 0] [0 0])))
    (is (= [23 0] (advance-time [23 0] [23 0])))
    (is (= [23 0] (advance-time [23 0] [11 0])))))

(deftest advance-minute
  (testing "simple advance"
    (is (= [10 1] (advance-time [10 0] [10 1])))
    (is (= [10 30] (advance-time [10 31] [10 30]))))
  (testing "advance to next hour"
    (is (= [11 0] (advance-time [10 59] [10 0])))
    (is (= [12 5] (advance-time [11 58] [11 5]))))
  (testing "go back to previous hour"
    (is (= [10 59] (advance-time [11 0] [11 59])))
    (is (= [11 55] (advance-time [12 5] [12 55])))))

(deftest advance-hour
  (testing "simple advance"
    (is (= [10 0] (advance-time [9 0] [10 0])))
    (is (= [10 0] (advance-time [11 0] [10 0])))
    (is (= [20 0] (advance-time [19 0] [8 0]))))
  (testing "am -> pm"
    (is (= [12 0] (advance-time [11 0] [0 0])))
    (is (= [13 0] (advance-time [11 0] [1 0])))
    (is (= [23 10] (advance-time [1 10] [11 10]))))
  (testing "pm -> am"
    (is (= [0 0] (advance-time [23 0] [0 0])))
    (is (= [1 0] (advance-time [23 0] [1 0])))))

(run-tests)
