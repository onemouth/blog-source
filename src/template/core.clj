(ns template.core
  (:require [template.default :as default]
            [babashka.fs :as fs]
            [hiccup.core :as hiccup :refer [html]]))

(defn- add-doctype [src]
  (str "<!doctype html>\n" src))

(defn- default-template []
  (-> (default/template)
      (html)
      (add-doctype)))

(defn- output-template [path template]
  (fs/write-lines path [template]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
;bb task
(defn gen []
  (let [path-template-pair [["templates/default.html" default-template]]]
    (doseq [[path template] path-template-pair]
      (println (str "generate " path))
      (output-template path (template)))))
