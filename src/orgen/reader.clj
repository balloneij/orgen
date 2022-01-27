(ns orgen.reader
  (:require [clojure.java.io :as io])
  (:import [java.io StringWriter StringReader]))

(def newline-c 10)
(def carriage-return-c 13)

(derive java.io.File ::file)
(derive java.lang.String ::string)

(defn- reader-dispatch [input]
  (class input))

(defmulti reader #'reader-dispatch)

(defmethod reader ::file [input-file]
  (io/reader input-file))

(defmethod reader ::string [input-str]
  (StringReader. input-str))

(defn mark [reader]
  (.mark reader 100))

(defn read
  ([reader] (read reader 1))
  ([reader amount]
   (let [sw (StringWriter.)]
     (loop [amount amount]
       (if (zero? amount)
         (.toString sw)
         (let [c (.read reader)]
           (if (not= c -1)
             (do
               (.write sw c)
               (recur (dec amount)))
             (recur 0))))))))

(defn skip
  ([reader] (skip reader 1))
  ([reader amount]
   (.skip reader amount)))

(defn skip-whitespace [reader]
  (mark reader)
  (loop [c (.read reader)]
    (case (char c)
      \space (do (mark reader) (recur (.read reader)))
      \tab (do (mark reader) (recur (.read reader)))
      \newline (do (mark reader) (recur (.read reader)))
      \return (do (mark reader) (recur (.read reader)))
      (.reset reader))))

(defn end-of-line? [reader]
  (mark reader)
  (let [c1 (char (.read reader))
        c2 (char (.read reader))
        eol? (or (= c1 \newline)
                 (and (= c1 \return) (= c2 \newline)))]
    (.reset reader)
    eol?))

(defn nth
  ([reader index] (nth reader index 1))
  ([reader index amount]
   (.mark reader (inc index))
   (if (= (.skip reader index) index)
     (let [result (read reader amount)]
       (.reset reader)
       result)
     (do
       (.reset reader)
       ""))))

(defn read-line [reader]
  (let [sw (StringWriter.)]
    (loop [c (.read reader)]
      (cond
        (= c -1) (.toString sw)
        (= c newline-c) (.toString sw)
        (and
         (= c carriage-return-c)
         (= (nth reader 0) newline)) (do (read reader) (.toString sw))
        (= c carriage-return-c) (.toString sw)
        :else (do
                (.write sw c)
                (recur (.read reader)))))))

(defn end-of-stream? [reader]
  (.mark reader 1)
  (let [end-of-stream? (= (.read reader) -1)]
    (.reset reader)
    end-of-stream?))
