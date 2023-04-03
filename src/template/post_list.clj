(ns template.post-list)

(defn template []
  [:ul {:class "list-disc list-inside pl-3 mb-10"}
   "\n$for(posts)$\n"
   [:li {:class "mb-3"}
    [:a {:href "$posts.url$"} "$posts.title$"] " - $posts.date$"]
   "\n$endfor$\n"])