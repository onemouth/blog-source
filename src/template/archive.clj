(ns template.archive
  (:require [template.default :as default]
            [template.post-list :as post-list]))

(defn template []
  [:div [:h1 {:class "index-header"} "Articles"]
   (post-list/template)])


(defn template-s []
  (default/template :embed (template)))