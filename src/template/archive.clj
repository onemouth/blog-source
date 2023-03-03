(ns template.archive
  (:require [template.post-list :as post-list]))

(defn template []
  [:div [:h2 "Articles"]
   (post-list/template)])