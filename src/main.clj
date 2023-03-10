(ns main
  (:require :require
            [babashka.fs :as fs]
            [clj-yaml.core :as yaml]
            [clojure.java.io :as io]
            [tick.core :as t]
            [clojure.string :as string]
            [compiler.copyfile :as copyfile]
            [compiler.pandoc :as pandoc]))

(def ^{:private true} config {})

(def ^{:private true} state (atom {}))

(defn output-dir []
  (:output-dir config "_site"))

(defn cache-dir []
  (:cacche-dir config "_cache"))

(defn list-folder
  ([root pattern]
   (map str (fs/glob root pattern)))
  ([root pattern opts]
   (map str (fs/glob root pattern opts))))

(defn cache-path [path]
  (io/file (cache-dir) path))

(defn output-path [path trans]
  (io/file (output-dir) (trans path)))

(defn prn-updated-msg [path]
  (println (str "updated " path)))

(defn build-images []
  (doseq [path (list-folder "images" "*")]
    (-> path
        (output-path identity)
        (copyfile/run-cp path)
        (prn-updated-msg))))

(defn build-css []
  (doseq [path (list-folder "css" "*.css")]
    (let [content (slurp path)]
      (-> path
          (output-path identity)
          (copyfile/run-content  content)
          (prn-updated-msg)))))

(defn build-nojekyll []
  (-> ".nojekyll"
      (output-path identity)
      (copyfile/run-content "")
      (prn-updated-msg)))

(defn store-posts-meta [posts]
  (swap! state assoc :posts posts)
  (-> "allposts.yaml"
      (cache-path)
      (copyfile/run-content (yaml/generate-string {:posts posts}))
      (prn-updated-msg))
  (-> "recentposts.yaml"
      (cache-path)
      (copyfile/run-content (yaml/generate-string {:posts (take 5 posts)}))
      (prn-updated-msg)))

(defn build-posts []
  (let [files (list-folder "posts" "*.md")
        posts (for [f files] (pandoc/parse-meta f))
        sorted-posts (sort-by :date-obj t/> posts)]
    (store-posts-meta sorted-posts)
    (doseq [meta sorted-posts]
      (-> (:path meta)
          (output-path #(string/replace %1 ".md" ".html"))
          (pandoc/run-post-html meta)
          (prn-updated-msg)))))

(defn build-archive-html []
  (-> "archive.html"
      (output-path identity)
      (pandoc/run-with-posts-meta "Archives" "templates/archive.html" "_cache/allposts.yaml")
      (prn-updated-msg)))

(defn build-index-html []
  (-> "index.html"
      (output-path identity)
      (pandoc/run-with-posts-meta "Home" "templates/index.html" "_cache/recentposts.yaml")
      (prn-updated-msg)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
; bb action
(defn build []
  (build-images)
  (build-css)
  (build-posts)
  (build-archive-html)
  (build-index-html)
  (build-nojekyll))