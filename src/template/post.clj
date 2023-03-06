(ns template.post
  (:require [template.default :as default]))

(defn template []
  [:div
   [:article
    [:h1 "$title$"]
    [:section.header
     "\nPosted on $date$"
     "\n$if(author)$"
     "\nby $author$"
     "\n$endif$\n"]
    [:section "$body$"]]
   [:comment
    [:script {:src "https://giscus.app/client.js"
              :data-repo "onemouth/onemouth.github.io"
              :data-repo-id "MDEwOlJlcG9zaXRvcnkyNjExMjI0MTE="
              :data-category "General"
              :data-category-id "DIC_kwDOD5Bpa84COAIg"
              :data-mapping "pathname"
              :data-reactions-enabled "0"
              :data-emit-metadata "0"
              :data-input-position "top"
              :data-theme "light"
              :data-lang "en"
              :crossorigin "anonymous"
              :async true}]
    [:script {:type "text/javascript"
              :src "https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.1/MathJax.js?config=TeX-MML-AM_CHTML"
              :async true}]]])

(defn template-s []
  (default/template :embed (template)))