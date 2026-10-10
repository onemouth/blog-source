(ns template.index
  (:require [template.default :as default]
            [template.post-list :as post-list]))

(defn template []
  [:div
   [:img {:src "/images/student.png" :style "float: right; margin: 10px; width: auto;"}]
   [:h1 {:class "index-header"} "About Me"]
   [:p "I am LT. Welcome to my blog!"]
   [:p "Contact me by email: lt at ltt.pw"]
   [:h2 {:class "index-header"} "Recent Posts"]
   (post-list/template)
   [:p "And you can find more " [:a {:href "/archive"} "here"]]])

(defn template-s []
  (default/template
   :embed (template)))