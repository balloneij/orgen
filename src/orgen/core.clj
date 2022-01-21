(ns orgen.core
  (:require [clojure.java.io :refer [reader]]
            [hiccup.core :refer [html]]
            [orgen.parser :refer [parse]]
            [orgen.converter :refer [converter]]))

(defn -main [& args]
  (if (seq? args)
    (-> (first args)
        (reader)
        (parse)
        (converter)
        (html)
        (println))
    (println "Usage: orgen myfile.org")))
