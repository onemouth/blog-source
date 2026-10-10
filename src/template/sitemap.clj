(ns template.sitemap
  (:require [clojure.string :as string]
            [hiccup2.core :refer [html]]))

(defn sitemap-xml [root posts]
  (let [root (string/replace root #"/+$" "")
        pages (concat [{:url "/"} {:url "/archive"}] posts)]
    (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
         (html {:mode :xml}
               [:urlset {:xmlns "http://www.sitemaps.org/schemas/sitemap/0.9"}
                (for [{:keys [url updated-date-obj]} pages]
                  [:url
                   [:loc (str root url)]
                   (when updated-date-obj [:lastmod (str updated-date-obj)])])])
         "\n")))
