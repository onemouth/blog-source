(ns site
  (:require [clojure.string :as string]))

(def config
  {:title "Put some ink into the inkpot"
   :author-name "LT Tsai"
   :timezone "+08:00"
   :root "https://lt.ichiban.day"
   :default-lang "zh-Hant"
   :home-description "LT's blog about programming, technology, languages, and travel."
   :archive-description "Browse all articles on LT's blog about programming, technology, languages, and travel."})

(defn absolute-url [path]
  (str (:root config) path))

(def ^:private og-locales {"zh-Hant" "zh_TW" "en" "en_US"})

(defn page-meta [{:keys [title summary url lang date-obj]} type]
  (let [lang (or lang (:default-lang config))]
    (cond-> {:page-title title
             :site-title (:title config)
             :page-description (or summary title)
             :canonical-url (absolute-url url)
             :page-type type
             :page-lang lang}
      (get og-locales lang)
      (assoc :og-locale (get og-locales lang))
      date-obj (assoc :published-date (str date-obj)))))

;; Pandoc template variables are literal strings, not Markdown metadata.
;; Escape once here so quotes, ampersands and angle brackets are safe in HTML.
(defn template-args [metadata]
  (mapcat (fn [[key value]]
            (when (some? value)
              ["-V" (str (name key) "="
                         (string/escape (str value)
                                        {\& "&amp;" \< "&lt;" \> "&gt;"
                                         \" "&quot;" \' "&#39;"}))]))
          metadata))
