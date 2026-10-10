(ns site
  (:require [clojure.string :as string]
            [cheshire.core :as json]))

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

(defn structured-data [page author]
  (let [url (:canonical-url page)
        article? (= "article" (:page-type page))]
    (when (or article? (= url (absolute-url "/")))
      (cond-> {"@context" "https://schema.org"
               "@type" (if article? "BlogPosting" "WebSite")
               "url" url
               "description" (:page-description page)
               "inLanguage" (:page-lang page)
               "author" {"@type" "Person"
                         "name" (or author (:author-name config))}}
        article? (assoc "headline" (:page-title page)
                        "mainEntityOfPage" {"@type" "WebPage" "@id" url}
                        "datePublished" (:published-date page))
        (not article?) (assoc "name" (:site-title page))))))

(defn json-ld [data]
  ;; A script element is raw text: HTML entities would corrupt JSON.
  ;; Escape '<' as JSON Unicode so embedded </script> cannot close the element.
  (string/replace (json/generate-string data) "<" "\\u003c"))

(defn page-meta [{:keys [title summary url lang date-obj author]} type]
  (let [lang (or lang (:default-lang config))
        page (cond-> {:page-title title
                      :site-title (:title config)
                      :page-description (or summary title)
                      :canonical-url (absolute-url url)
                      :page-type type
                      :page-lang lang}
               (get og-locales lang) (assoc :og-locale (get og-locales lang))
               date-obj (assoc :published-date (str date-obj)))
        data (structured-data page author)]
    (cond-> page
      data (assoc :json-ld (json-ld data)))))

;; Pandoc template variables are literal strings, not Markdown metadata.
;; HTML values are escaped once; JSON-LD is already serialized for raw script text.
(defn template-args [metadata]
  (mapcat (fn [[key value]]
            (when (some? value)
              ["-V" (str (name key) "="
                         (if (= key :json-ld)
                           value
                           (string/escape (str value)
                                          {\& "&amp;" \< "&lt;" \> "&gt;"
                                           \" "&quot;" \' "&#39;"})))]))
          metadata))
