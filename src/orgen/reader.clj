(ns orgen.reader
  (:require [clojure.java.io :as io])
  (:import [java.io StringWriter StringReader]))

(derive java.io.File ::file)
(derive java.lang.String ::string)

(defn- reader-dispatch [input]
  (class input))

(defmulti reader #'reader-dispatch)

(defmethod reader ::file [input-file]
  (io/reader input-file))

(defmethod reader ::string [input-str]
  (StringReader. input-str))

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

(defn nth
  ([reader index] (nth reader index 1))
  ([reader index amount]
   (.mark reader (inc index))
   (if (= (.skip reader index) index)
     (let [result (read reader amount)]
       (.reset reader)
       result)
     "")))
