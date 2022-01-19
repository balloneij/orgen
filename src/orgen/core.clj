(ns orgen.core
  (:require [clojure.java.io :refer [reader]]
            [orgen.parser :refer [parse]]
            [orgen.converter :refer [converter]]))

(defn -main [& args]
  (if (seq? args)
    (println (converter (parse (reader (first args)))))
    (println "Usage: orgen myfile.org")))
