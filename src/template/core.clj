(ns template.core
  (:require [babashka.fs :as fs]
            [hiccup2.core :as hiccup :refer [html]]
            [template.archive :as archive]
            [template.default :as default]
            [template.index :as index]
            [template.post :as post]
            [template.revealjs :as revealjs]))

(defn- add-doctype [src]
  (str "<!doctype html>\n" src))

(defn- default-template []
  (-> (default/template)
      (html)
      (str)
      (add-doctype)))

(defn archive-template []
  (-> (archive/template-s)
      (html)
      (str)))

(defn post-template []
  (-> (post/template-s)
      (html)
      (str)))

(defn index-template []
  (-> (index/template-s)
      (html)
      (str)))

(defn revealjs-template []
  (-> (revealjs/template)
      (html)
      (str)))

(defn- output-template [path template]
  (fs/write-lines path [template]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
;bb task
(defn gen []
  (let [path-template-pair [["templates/default.html" default-template]
                            ["templates/post.html" post-template]
                            ["index.html" index-template]
                            ["templates/revealjs.html" revealjs-template]
                            ["templates/archive.html" archive-template]]]
    (doseq [[path template] path-template-pair]
      (println (str "generate " path))
      (output-template path (template)))))
