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

(def ^{:private true} rss-config
  {:title "Put some ink into the inkpot"
   :author-name "LT Tsai"
   :timezone "+08:00"
   :root "https://lt.ichiban.day"})

(defn- rss-content [html-path]
  (let [content (:out (sh "htmlq" "-f" html-path "main"))]
    content))

(defn- get-entries [context]
  (for [post (:posts @context)]
    (let  [title (:title post)
           feed-root (:root rss-config)
           path (:path post)
           url (string/join "/" [feed-root (string/replace path ".md" ".html")])
           timezone (:timezone rss-config)
           published (format "%sT12:00:00%s" (:date-obj post) timezone)
           updated published
           content (rss-content (:dest post))]
      (rss/atom-entry title url published updated content))))

(defn- build-rss [context]
  (let [feed-title (:title rss-config)
        author-name (:author-name rss-config)
        feed-root (:root rss-config)
        now-str (t/instant)
        entries (get-entries context)
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

(defmethod process [:create-file false true] [{:keys [file content route cache]}]
  (let [create-file (if cache cache-file #(output-file %1 route))]
    (-> file
        (create-file)
        (cp/copy-content content)
        (prn-updated-msg))))

(defmethod process [:pandoc-embed false true] [{:keys [file title template-file meta-file]}]
  (let [dest (string/join "/" [(output-dir) file])
        cmd  ["pandoc"
              "-M" (str "title=" title)
              (str "--metadata-file=" meta-file)
              (str "--template=" template-file)
              "-o" dest]]
    (println (string/join " " cmd))
    (apply sh {:in ""} cmd)
    dest))

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
    (swap! context assoc :posts sorted-posts-meta)
    (doseq [[idx meta] (map-indexed vector sorted-posts-meta)]
      (-> (:path meta)
          (output-file route)
          (run-pandoc-post template-file toc-enabled-template-file toc-args basic-args meta)
          (prn-updated-msg)
          (assoc-dest-meta-by-idx idx context)))))


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
                           "-L" "lua/external_links.lua"
                           "-L" "lua/sidenotes.lua"
                           "--mathjax"
                           "-t" "html"
                           "-f" "markdown+east_asian_line_breaks+footnotes"]
              :context context})
    (process {:action :create-file
              :file "allposts.yaml"
              :cache true
              :content (yaml/generate-string @context)})
    (process {:action :create-file
              :file "recentposts.yaml"
              :cache true
              :content (yaml/generate-string
                        {:posts (take 10 (:posts @context))})})
    (process {:action :pandoc-embed
              :template-file "templates/archive.html"
              :file "archive.html"
              :meta-file (string/join "/" [(cache-dir) "allposts.yaml"])
              :title "Archive"})
    (process {:action :pandoc-embed
              :template-file "templates/index.html"
              :file "index.html"
              :meta-file (string/join "/" [(cache-dir) "recentposts.yaml"])
              :title "Home"})
    (build-rss context)))



