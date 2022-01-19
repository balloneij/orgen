(ns orgen.converter
  (:require [clojure.string :as str]
            [clojure.walk :refer [prewalk]]))

(defn child [parent i]
  (nth parent i nil))

(defn tag= [node value]
  (if (sequential? node)
    (= (first node) value)
    nil))

(defn right-text-siblings [parent i]
  (loop [i (inc i)
         siblings []]
    (let [next-sibling (child parent i)]
      (if (tag= next-sibling :text)
        (recur (inc i) (conj siblings next-sibling))
        siblings))))

(defn text->paragraph [parent i node]
  (if (tag= node :text)
    (let [left-sibling (child parent (dec i))]
      (if-not (tag= left-sibling :text)
        [:p (str/join " "
                      (map (fn [node]
                                (second node))
                              (concat [node] (right-text-siblings parent i))))]
        :ignore-tag))
    nil))

(defn pass-1 [node]
  (if (and (sequential? node) (not (map-entry? node)))
    (reduce-kv (fn [m i v]
                 (let [result (or (text->paragraph node i v)
                                  v)]
                   (if (= result :ignore-tag)
                     m
                     (conj m result))))
               []
               node)
    node))

(defn converter [document]
  (prewalk pass-1 document))
