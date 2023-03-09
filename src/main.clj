(ns main
  (:require :require
            [babashka.fs :as fs]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [compiler.copyfile :as copyfile]
            [compiler.pandoc :as pandoc]
            [template.core :as template]))

(def ^{:private true} config {})

(def ^{:private true} state (atom {}))

(defn output-dir []
  (:output-dir config "_site"))

(defn list-folder
  ([root pattern]
   (map str (fs/glob root pattern)))
  ([root pattern opts]
   (map str (fs/glob root pattern opts))))

(defn output-path [path f]
  (io/file (output-dir) (f path)))

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

(defn build-posts []
  (let [files (list-folder "posts" "*.md")
        posts (for [f files] (pandoc/parse-meta f))
        sorted-posts (sort-by :date posts)]
    (swap! state assoc :posts sorted-posts)
    (doseq [meta sorted-posts]
      (-> (:path meta)
          (output-path #(string/replace %1 ".md" ".html"))
          (pandoc/run-post-html meta)
          (prn-updated-msg)))))

(defn build-archive-html []
  (let [posts (:posts @state)]
    (prn posts)
    (-> "archive.html"
        (output-path identity)
       ;(copyfile/run-content (template/archive-template posts))
        (prn-updated-msg))))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
; bb action
(defn build []
  (build-images)
  (build-css)
  (build-posts)
  ;(build-archive-html)
  (build-nojekyll))