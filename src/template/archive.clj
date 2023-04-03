(ns template.archive
  (:require [template.default :as default]
            [template.post-list :as post-list]
            [template.tailwind :refer [index-header]]))

(defn template []
  [:div [:h2 {:class (index-header)} "Articles"]
   (post-list/template)])


(defn template-s []
  (default/template :embed (template)))