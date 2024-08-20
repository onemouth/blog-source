(ns compiler.pandoc
  (:require
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
