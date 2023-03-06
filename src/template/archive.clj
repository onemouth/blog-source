(ns template.archive
  (:require [template.default :as default]
            [template.post-list :as post-list]))

(defn template []
  [:div [:h2 "Articles"]
   (post-list/template)])


(defn template-s []
  (default/template :embed (template)))