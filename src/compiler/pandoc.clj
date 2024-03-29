(ns compiler.pandoc
  (:require [babashka.process :refer [sh]]
            [clojure.java.io :as io]
            [tick.core :as t]
            [clj-yaml.core :as yaml]
            [clojure.string :as string]))

(defn- get-yaml-header-helper [lines]
  (loop [lines lines
         state :init
         res []]
    (if-let [curr (first lines)]
      (case state
        :init (if (string/starts-with? curr "---")
                (recur (rest lines) :collect res)
                res)
        :collect (if (string/starts-with? curr "---")
                   res
                   (recur (rest lines) :collect (conj res curr))))

      res)))

(defn- get-yaml-header [path]
  (with-open [rdr (io/reader path)]
    (let [lines (line-seq rdr)]
      (string/join "\n"
                   (get-yaml-header-helper lines)))))

;(get-yaml-header "posts/2022-05-03-icloud-cool-things.md")

(defn- yaml-header->map [path]
  (-> path
      (get-yaml-header)
      (yaml/parse-string)))

(defn- path-date [path]
  (re-find #"\d{4}-\d{2}-\d{2}" path))

(defn- path->url [path]
  (str "/" (string/replace path "md" "html")))

;(yaml-header->edn "posts/2022-03-10-japanese-plugins.md")

(defn parse-meta [path]
  (let [header-map (yaml-header->map path)
        date-obj (t/date (path-date path))
        date (t/format "MMM dd, yyyy" date-obj)
        header-map (if date (assoc header-map :date date :date-obj date-obj) header-map)]
    (assoc header-map :path path :url (path->url path))))

(defn run-post-html [dest
                     {:keys [path date enable]}]
  (io/make-parents dest)
  (let [toc-enable (:toc enable)
        template-file (if toc-enable "templates/post-toc.html" "templates/post.html")
        template-cmd [(str "--template=" template-file)]
        basic-cmd (concat ["pandoc"
                           "-s"
                           "--mathjax"
                           "-t" "html"
                           "-f" "markdown+east_asian_line_breaks"
                           path
                           "-o" (str dest)
                           "-M" (str "date=" date)] template-cmd)
        toc-args ["--toc"
                  "--number-sections"
                  "--toc-depth=2"]
        cmd (if toc-enable (concat basic-cmd toc-args) basic-cmd)]
    (println (string/join " " cmd))
    (apply sh cmd))
  dest)

(defn run-with-posts-meta [dest title template-file meta-path]
  (let [cmd (concat ["pandoc"
                     "-M" (str "title=" title)
                     (str "--metadata-file=" meta-path)
                     (str "--template=" template-file)
                     "-o" (str dest)])]
    (println (string/join " " cmd))
    (apply sh {:in ""} cmd)
    dest))