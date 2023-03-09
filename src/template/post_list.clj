(ns template.post-list)

(defn template []
  [:ul
   "\n$for(posts)$\n"
   [:li
    [:a {:href "$posts.url$"} "$posts.title$"] " - $posts.date$"]
   "\n$endfor$\n"])