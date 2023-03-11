(ns template.rss
  (:require [hiccup2.core :as hiccup :refer [html]]))

(defn atom-template-xml [title author-name root updated entries {:keys [author-email]}]
  (html {:mode :xml}
        (let [file-id (str root "/atom.xml")]
          [:feed {:xmlns "http://www.w3.org/2005/Atom"}
           [:title title]
           [:link {:href file-id :rel "self"}]
           [:link {:href root}]
           [:id file-id]
           [:author
            [:name author-name]
            (if author-email [:email "$authorEmail$"] "")]
           [:updated updated]
           (for [entry entries]
             entry)])))

(defn- cdata [content]
  (format "<![CDATA[%s]]>" content))

(defn atom-entry [title path published updated content]
  ;(println content)
  (let [file-id (str path "/atom.xml")]
    [:entry
     [:title title]
     [:link {:href file-id}]
     [:id file-id]
     [:published published]
     [:updated updated]
     [:summary {:type "html"} (cdata content)]]))

