(ns orgen.parser-test
  (:require [orgen.parser :as sut]
            [orgen.reader :as r]
            [clojure.test :refer :all]))

(deftest parse-heading-test
  (testing "parse non heading"
    (let [reader (r/reader "*My heading")]
      (is (nil? (sut/parse-heading reader)))))

  (testing "parse level 1 heading"
    (let [reader (r/reader "* My heading")]
      (is (= [:heading {:level 1} "My heading"]
             (sut/parse-heading reader)))))

  (testing "parse level 2 heading"
    (let [reader (r/reader "** My heading")]
      (is (= [:heading {:level 2} "My heading"]
             (sut/parse-heading reader))))))
