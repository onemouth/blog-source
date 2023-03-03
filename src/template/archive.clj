(ns template.archive)

(defn template []
  [:div [:h2 "Articles"]
   "\n$partial(\"templates/post-list.html\")$\n"])