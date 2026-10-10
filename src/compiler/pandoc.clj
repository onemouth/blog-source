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
  (let [header (get-yaml-header path)
        metadata (yaml/parse-string header)]
    ;; YAML coerces unquoted dates to java.util.Date and may normalize invalid
    ;; days (e.g. Feb 30). Recover the original scalar for strict validation.
    (if (instance? java.util.Date (:updated_date metadata))
      (let [marked (yaml/parse-string header :mark true)
            node (->> (:unmark marked)
                      (filter (fn [[key _]] (= "updated_date" (:unmark key))))
                      last
                      second)
            value (subs header (get-in node [:start :index])
                               (get-in node [:end :index]))]
        (assoc metadata :updated_date value))
      metadata)))

(defn- path-date [path]
  (re-find #"\d{4}-\d{2}-\d{2}" path))

(defn- path->url [path]
  (str "/" (string/replace path #"\.md$" "")))

;(yaml-header->edn "posts/2022-03-10-japanese-plugins.md")

(defn- parse-updated-date [value published path]
  (when (some? value)
    (let [invalid #(ex-info (str path ": updated_date must be a valid YYYY-MM-DD date")
                           {:path path :field :updated_date :value value})
          date (if (and (string? value) (re-matches #"[0-9]{4}-[0-9]{2}-[0-9]{2}" value))
                 (try
                   (java.time.LocalDate/parse value)
                   (catch java.time.format.DateTimeParseException _
                     (throw (invalid))))
                 (throw (invalid)))]
      (when (t/< date published)
        (throw (ex-info (str path ": updated_date cannot be before publication (" published ")")
                        {:path path :field :updated_date :value value
                         :published-date (str published)})))
      date)))

(defn parse-meta [path]
  (let [header-map (yaml-header->map path)
        date-obj (t/date (path-date path))
        updated-date-obj (parse-updated-date (:updated_date header-map) date-obj path)
        date (t/format "MMM dd, yyyy" date-obj)]
    (cond-> (assoc header-map :date date :date-obj date-obj
                             :path path :url (path->url path))
      updated-date-obj (assoc :updated-date-obj updated-date-obj))))
