(ns template.post-list)

(defn template []
  [:ul {:class "list-disc list-inside"}
   "\n$for(posts)$\n"
   [:li
    [:a {:href "$posts.url$"} "$posts.title$"] " - $posts.date$"]
   "\n$endfor$\n"])