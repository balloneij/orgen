(ns orgen.parser
  (:require [clojure.string :as str]
            [orgen.reader :as r]))

(defn- heading-level [reader]
  (loop [level 0]
    (if (= (r/nth reader level) "*")
      (recur (inc level))
      level)))

(defn parse-line [reader]
  [:text (r/read-line reader)])

(defn parse-heading [reader]
  (let [level (heading-level reader)]
    (if (and (pos? level) (str/blank? (r/nth reader level)))
      (do
        (r/skip reader level)
        (r/skip-whitespace reader)
        [:heading {:level level} (parse-line reader)])
      nil)))

(defn parse-attributes [reader]
  (let [text (r/nth-start reader 0 2)]
    ;; TODO Use regex
    (if (and (= text "#+") (not (str/blank? (r/nth-start reader 2))))
      (do
        (r/read-line reader)
        nil)
      nil)))

(defn parse-next [reader]
  (or
   (parse-heading reader)
   (parse-attributes reader)
   (parse-line reader)))

(defn parse [reader]
  (loop [document [:document]]
    (if (r/end-of-stream? reader)
      document
      (recur (conj document (parse-next reader))))))
