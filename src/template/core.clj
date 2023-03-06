(ns template.core
  (:require [template.default :as default]
            [template.archive :as archive]
            [template.revealjs :as revealjs]
            [template.post :as post]
            [template.post-list :as post-list]
            [babashka.fs :as fs]
            [hiccup2.core :as hiccup :refer [html]]))

(defn- add-doctype [src]
  (str "<!doctype html>\n" src))

(defn- default-template []
  (-> (default/template)
      (html)
      (str)
      (add-doctype)))

(defn archive-template []
  (-> (archive/template)
      (html)
      (str)))

(defn post-list-template []
  (-> (post-list/template)
      (html)
      (str)))

(defn post-template []
  (-> (post/template)
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
                            ["templates/post-list.html" post-list-template]
                            ["templates/post.html" post-template]
                            ["templates/revealjs.html" revealjs-template]
                            ["templates/archive.html" archive-template]]]
    (doseq [[path template] path-template-pair]
      (println (str "generate " path))
      (output-template path (template)))))
