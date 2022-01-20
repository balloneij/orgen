(ns orgen.converter
  (:require [clojure.string :as str]
            [clojure.walk :refer [postwalk]]))

(defn child [parent i]
  (nth parent i nil))

(defn tag= [node tag]
  (if (sequential? node)
    (= (first node) tag)
    nil))

(defn node-value [node]
  (if (map? (second node))
    (nthrest node 2)
    (rest node)))

(defn node-attrs [node]
  (let [attrs (second node)]
    (if (map? attrs)
      attrs
      {})))

(defn right-text-siblings [parent i]
  (loop [i (inc i)
         siblings []]
    (let [next-sibling (child parent i)]
      (if (tag= next-sibling :text)
        (recur (inc i) (conj siblings next-sibling))
        siblings))))

(defn text->paragraph [parent i node]
  (if (and (tag= node :text) (not (tag= parent :heading)))
    (let [left-sibling (child parent (dec i))]
      (if-not (tag= left-sibling :text)
        [:p (str/join " "
                      (map (fn [node]
                                (second node))
                              (concat [node] (right-text-siblings parent i))))]
        :ignore-tag))
    nil))

(defn heading->hx [node]
  (if (tag= node :heading)
    (let [{:keys [level]} (node-attrs node)]
      (vec (concat [(keyword (str "h" level))] (node-value node))))
    nil))

(defn pass-1 [node]
  (if (and (sequential? node) (not (map-entry? node)))
    (reduce-kv (fn [m i v]
                 (let [result (or (text->paragraph node i v)
                                  (heading->hx v)
                                  v)]
                   (if (= result :ignore-tag)
                     m
                     (conj m result))))
               []
               node)
    node))

(defn converter [document]
  (postwalk pass-1 document))
