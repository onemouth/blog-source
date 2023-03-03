(ns main
  (:require :require
            [babashka.fs :as fs]
            [clojure.java.io :as io]
            [compiler.copyfile :as copyfile]))

(def ^{:private true} config {})

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
    (let [content (slurp path)]
      (-> path
          (output-path identity)
          (copyfile/run  content)
          (prn-updated-msg)))))

(defn build-css []
  (doseq [path (list-folder "css" "*.css")]
    (let [content (slurp path)]
      (-> path
          (output-path identity)
          (copyfile/run  content)
          (prn-updated-msg)))))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
; bb action
(defn build []
  (build-images)
  (build-css))