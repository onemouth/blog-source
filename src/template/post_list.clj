(ns template.post-list)

(defn template []
  [:ul {:class "post-list"}
   "\n$for(posts)$\n"
   [:li {:class "post-list-item"}
    [:a {:href "$posts.url$"} "$posts.title$"] " - $posts.date$"]
   "\n$endfor$\n"])