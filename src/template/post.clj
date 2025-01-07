(ns template.post
  (:require [template.default :as default]))

(defn template [enable-toc]
  [:div
   [:article
    [:h1 "$title$"]
    [:section.header
     "\nPosted on $date$"
     "\n$if(author)$"
     "\nby $author$"
     "\n$endif$\n"]
    [:section
     (if enable-toc
       [:div
        [:div.toc
         [:div.header "Table of Contents"]
         "$toc$"]
        [:div
         "$body$"]]
       "$body$")]]
   [:comment
    [:script {:type "text/javascript"
              :src "https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.1/MathJax.js?config=TeX-MML-AM_CHTML"
              :async true}]]])

(defn template-s [enable-toc]
  (default/template :embed (template enable-toc)))