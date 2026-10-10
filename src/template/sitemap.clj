(ns template.sitemap
  (:require [clojure.string :as string]
            [hiccup2.core :refer [html]]))

(defn sitemap-xml [root posts]
  (let [root (string/replace root #"/+$" "")
        paths (concat ["/" "/archive"] (map :url posts))]
    (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
         (html {:mode :xml}
               [:urlset {:xmlns "http://www.sitemaps.org/schemas/sitemap/0.9"}
                (for [path paths]
                  [:url [:loc (str root path)]])])
         "\n")))
