(ns orgen.core
  (:require [clojure.java.io :refer [reader]]
            [orgen.parser :refer [parse]]))

(defn -main [& args]
  (if (seq? args)
    (println (parse (reader (first args))))
    (println "Usage: orgen myfile.org")))
