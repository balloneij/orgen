(ns orgen.reader-test
  (:require [orgen.reader :as r]
            [clojure.test :refer :all]))

(deftest read-test
  (testing "read 1 character"
    (let [reader (r/reader "oranges")]
      (is (= "o" (r/read reader)))))

  (testing "read 2 characters"
    (let [reader (r/reader "oranges")]
      (is (= "or" (r/read reader 2)))))

  (testing "read empty reader"
    (let [reader (r/reader "")]
      (is (= "" (r/read reader)))))

  (testing "read past reader"
    (let [reader (r/reader "oranges")
          result (r/read reader 500)]
      (is (= "oranges" result)))))

(deftest nth-test
  (testing "nth 0 character"
    (let [reader (r/reader "oranges")]
      (is (= "o" (r/nth reader 0)))))

  (testing "nth 1 character"
    (let [reader (r/reader "oranges")]
      (is (= "r" (r/nth reader 1)))))

  (testing "amount from nth character"
    (let [reader (r/reader "oranges")]
      (is (= "ang" (r/nth reader 2 3)))))

  (testing "nth does not modify the reader position"
    (let [reader (r/reader "oranges")]
      (r/nth reader 2)
      (r/nth reader 100)
      (r/nth reader 2)
      (r/nth reader 3 1000)
      (is (= "o" (r/read reader 1)))))

  (testing "nth past reader"
    (let [reader (r/reader "oranges")]
      (is (= "" (r/nth reader 500))))))
