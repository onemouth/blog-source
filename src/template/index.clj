(ns template.index
  (:require [template.post-list :as post-list]
            [template.default :as default]))

(defn template []
  [:div
   [:img {:src "/images/student.png" :style "float: right; margin: 10px; width: auto;"}]
   [:h2 "About Me"]
   [:p "I am LT. Welcome to my blog!"]
   [:h2 "Recently Posts:"]
   (post-list/template)
   [:p "And you can find more " [:a {:href "/archive.html"} "here"]]])

(defn template-s []
  (default/template
   :title "Home"
   :embed (template)))