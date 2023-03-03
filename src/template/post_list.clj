(ns template.post-list)

(defn template []
  [:ul
   "\n$for(posts)$\n"
   [:li
    [:a {:href "$url$"} "$title$"] " - $date$"]
   "\n$endfor$\n"])