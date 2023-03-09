(ns compiler.pandoc
  (:require [babashka.process :refer [sh]]
            [clojure.java.io :as io]
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

;(yaml-header->edn "posts/2022-03-10-japanese-plugins.md")

(defn parse-meta [path]
  (let [header-map (yaml-header->map path)
        date (path-date path)
        header-map (if date (assoc header-map :date date) header-map)]
    (assoc header-map :path path)))

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