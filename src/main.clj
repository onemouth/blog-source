(ns main
  (:require [babashka.fs :as fs]
            [babashka.process :refer [sh]]
            [clj-yaml.core :as yaml]
            [clojure.java.io :as io]
            [tick.core :as t]
            [clojure.string :as string]
            [compiler.cp :as cp]
            [compiler.pandoc :as pandoc]
            [template.rss :as rss]))

(def ^{:private true} config {})

(def ^{:private true} state (atom {}))

(defn output-dir []
  (:output-dir config "_site"))

(defn cache-dir []
  (:cache-dir config "_cache"))

(defn list-folder
  ([root pattern]
   (map str (fs/glob root pattern)))
  ([root pattern opts]
   (map str (fs/glob root pattern opts))))

(defn cache-file [path]
  (io/file (cache-dir) path))

(defn output-file [path trans]
  (io/file (output-dir) (trans path)))

(defn prn-updated-msg [path]
  (println (str "updated " path))
  path)

(defn build-images []
  (doseq [path (list-folder "images" "*")]
    (-> path
        (output-file identity)
        (cp/copy-file path)
        (prn-updated-msg))))

(defn build-css []
  (doseq [path (list-folder "css" "*.css")]
    (let [content (slurp path)]
      (-> path
          (output-file identity)
          (cp/copy-content  content)
          (prn-updated-msg)))))

(defn build-nojekyll []
  (-> ".nojekyll"
      (output-file identity)
      (cp/copy-content "")
      (prn-updated-msg)))

(defn build-into-html []
  (let [content (slurp "templates/intro.html")]
    (-> "intro.html"
        (output-file identity)
        (cp/copy-content content)
        (prn-updated-msg))))

(defn store-posts-meta [posts]
  (swap! state assoc :posts posts)
  (-> "allposts.yaml"
      (cache-file)
      (cp/copy-content (yaml/generate-string {:posts posts}))
      (prn-updated-msg))
  (-> "recentposts.yaml"
      (cache-file)
      (cp/copy-content (yaml/generate-string {:posts (take 10 posts)}))
      (prn-updated-msg)))

(defn- assoc-dest-meta [dest idx]
  (swap! state assoc-in [:posts idx :dest] dest))

(defn- build-archive-html []
  (-> "archive.html"
      (output-file identity)
      (pandoc/run-with-posts-meta "Archives" "templates/archive.html" "_cache/allposts.yaml")
      (prn-updated-msg)))

(defn- build-index-html []
  (-> "index.html"
      (output-file identity)
      (pandoc/run-with-posts-meta "Home" "templates/index.html" "_cache/recentposts.yaml")
      (prn-updated-msg)))

(def ^{:private true} rss-config
  {:title "Put some ink into the inkpot"
   :author-name "LT Tsai"
   :timezone "+08:00"
   :root "https://onemouth.github.io"})

(defn- rss-content [html-path]
  (let [content (:out (sh "htmlq" "-f" html-path "main"))]
    content))

(defn- get-entries []
  (for [post (:posts @state)]
    (let  [title (:title post)
           feed-root (:root rss-config)
           path (:path post)
           url (string/join "/" [feed-root (string/replace path ".md" ".html")])
           timezone (:timezone rss-config)
           published (format "%sT12:00:00%s" (:date-obj post) timezone)
           updated published
           content (rss-content (:dest post))]
      (rss/atom-entry title url published updated content))))

(defn- build-rss []
  (let [feed-title (:title rss-config)
        author-name (:author-name rss-config)
        feed-root (:root rss-config)
        now-str (t/instant)
        entries (get-entries)
        xml-content (-> (rss/atom-template-xml feed-title author-name feed-root now-str entries rss-config)
                        (str))]
    (-> "atom.xml"
        (output-file identity)
        (cp/copy-content xml-content)
        (prn-updated-msg))))


(defmulti process (juxt
                   :action
                   #(contains? %1 :folder)
                   #(contains? %1 :file)))

(defmethod process [:copy true false] [{:keys [folder route]}]
  (doseq [path (list-folder (get folder 0) (get folder 1))]
    (-> path
        (output-file route)
        (cp/copy-file path)
        (prn-updated-msg))))

(defmethod process [:create-file false true] [{:keys [file route content]}]
  (-> file
      (output-file route)
      (cp/copy-content content)
      (prn-updated-msg)))

(defn- assoc-dest-meta-by-idx [dest idx context]
  (swap! context assoc-in [:posts idx :dest] dest))

(defn- run-pandoc-post
  [dest template-file toc-enabled-template-file toc-args basic-args {:keys [path date enable]}]
  (io/make-parents dest)
  (let [toc-enable (:toc enable)
        template (if toc-enable toc-enabled-template-file template-file)
        base-cmd (concat
                  ["pandoc"]
                  basic-args
                  [path
                   "-o" (str dest)
                   "-M" (str "date=" date)
                   "--template" template])
        cmd (if toc-enable (concat base-cmd toc-args) base-cmd)]
    (println (string/join " " cmd))
    (apply sh cmd))
  dest)

(defmethod process
  [:pandoc-posts true false]
  [{:keys [folder route template-file toc-enabled-template-file toc-args basic-args context]}]
  (let [posts (list-folder (get folder 0) (get folder 1))
        sorted-posts-meta (vec
                           (sort-by :date-obj t/> (for [f posts] (pandoc/parse-meta f))))]
    (doseq [[idx meta] (map-indexed vector sorted-posts-meta)]
      (-> (:path meta)
          (output-file route)
          (run-pandoc-post template-file toc-enabled-template-file toc-args basic-args meta)
          (prn-updated-msg)
          (assoc-dest-meta-by-idx idx context)))))

(defn build-posts []
  (let [files (list-folder "posts" "*.md")
        posts (for [f files] (pandoc/parse-meta f))
        sorted-posts (vec (sort-by :date-obj t/> posts))]
    (store-posts-meta sorted-posts)
    (doseq [[idx meta] (map-indexed vector sorted-posts)]
      (-> (:path meta)
          (output-file #(string/replace %1 ".md" ".html"))
          (pandoc/run-post-html meta)
          (prn-updated-msg)
          (assoc-dest-meta idx)))))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
; bb action
(defn build-v2 []
  (let [context (atom {})]
    (process {:action :copy
              :folder ["images", "*"]
              :route identity})
    (process {:action :copy
              :folder ["css", "*.css"]
              :route identity})
    (process {:action :create-file
              :file ".nojekyll"
              :content ""
              :route identity})
    (process {:action :pandoc-posts
              :folder ["posts", "*.md"]
              :route #(string/replace %1 ".md" ".html")
              :template-file "templates/post.html"
              :toc-enabled-template-file "templates/post-toc.html"
              :toc-args ["--toc" "--number-sections" "--toc-depth=2"]
              :basic-args ["-s"
                           "-L" "lua/image_relative_url.lua"
                           "--mathjax"
                           "-t" "html"
                           "-f" "markdown+east_asian_line_breaks"]
              :context context})
    (build-archive-html)
    (build-index-html)
    (build-rss)))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
; bb action
(defn build-v1 []
  (build-images)
  (build-css)
  (build-posts)
  (build-archive-html)
  (build-index-html)
  (build-rss)
  (build-into-html)
  (build-nojekyll))

