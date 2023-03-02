#!/usr/bin/env bb

(require '[hiccup.core :as hiccup :refer [html]]
         '[template.default :as default])


(defn add-doctype [src]
  (str "<!doctype html>\n" src))

(defn -main [& args]
  (-> (default/template)
      (html)
      (add-doctype)
      (println)))


(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))