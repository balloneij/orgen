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

(defn text->text
  "Org text -> HTML text"
  [node]
  (str/join " " (node-value node)))

(defn text->? [parent i node]
  (if (tag= node :text)
    (cond
      (tag= parent :heading) (text->text node)
      :else (text->paragraph parent i node))))

(defn heading->hx [node]
  (if (tag= node :heading)
    (let [{:keys [level]} (node-attrs node)]
      (apply conj [(keyword (str "h" level))] (node-value node)))
    nil))

(defn document->html+body [document]
  [:html
   (apply conj [:body] (node-value document))])

(defn- -org->html [node]
  (if (and (sequential? node) (not (map-entry? node)))
    (reduce-kv (fn [m i v]
                 (let [result (or (text->? node i v)
                                  (heading->hx v)
                                  v)]
                   (if (= result :ignore-tag)
                     m
                     (conj m result))))
               []
               node)
    node))

(defn org->html [document]
  (document->html+body (postwalk -org->html document)))

(defn html->better-html [document]
  document)

(defn converter [document]
  (-> document
      (org->html)
      (html->better-html)))
