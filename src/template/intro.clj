(ns template.intro)

(defn template-s []
  [:html {:lang "en"}
   [:head
    [:link {:rel "stylesheet" :href "/css/output.css"}]]
   [:body
    [:div {:class "flex justify-center bg-gray-300"}
     [:div {:class "mx-4 order-last"}
      [:img {:src "images/music.svg" :size "100x100"}]]
     [:div {:class "mx-4 self-center text-center"}
      [:h1 {:class "text-6xl font-bold text-blue-700"} "Welcome to NorthBy"]
      [:h2 {:class "text-3xl font-semibold text-blue-300"} "A premium in sight and sound"]
      [:button {:class "my-4 px-4 py-2 border-2 border-black rounded-lg text-white bg-blue-900"} "Learn More"]]]]])