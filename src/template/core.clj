(ns template.core
  (:require [babashka.fs :as fs]
            [hiccup2.core :as hiccup :refer [html]]
            [template.archive :as archive]
            [template.index :as index]
            [template.intro :as intro]
            [template.post :as post]))

(defn archive-template []
  (-> (archive/template-s)
      (html)
      (str)))

(defn post-template []
  (-> (post/template-s false)
      (html)
      (str)))

(defn post-toc-template []
  (-> (post/template-s true)
      (html)
      (str)))

(defn index-template []
  (-> (index/template-s)
      (html)
      (str)))

(defn intro-template []
  (-> (intro/template-s)
      (html)
      (str)))

(defn- output-template [path template]
  (fs/write-lines path ["<!DOCTYPE html>" template]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
;bb task
(defn gen []
  (let [path-template-pair [["templates/post.html" post-template]
                            ["templates/post-toc.html" post-toc-template]
                            ["templates/index.html" index-template]
                            ["templates/intro.html" intro-template]
                            ["templates/archive.html" archive-template]]]
    (doseq [[path template] path-template-pair]
      (println (str "generate " path))
      (output-template path (template)))))
