(ns template.default)


(defn template [& {:keys [embed title]}]
  [:html {:lang "en"}
   [:head
    [:meta {:charset "utf-8"}]
    ; viewport settings for responsive design
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    ; SEO related setting
    "\n$if(tags)$\n"
    [:meta {:name "keywords" :content "$tags$"}]
    "\n$endif$\n"
    "$if(summary)$\n"
    [:meta {:name "description" :content "$summary$"}]
    "\n$endif$\n"
    [:title "Put some ink into the inkpot - " (if title title "$title$")]
    ;atom.xml
    [:link {:href "atom.xml" :type "application/atom+xml" :rel "alternate" :title "Put some ink into the inkpot"}]
    [:link {:rel "stylesheet" :href "/css/output.css"}]
    [:link {:rel "stylesheet" :href "/css/default.css"}]
    [:link {:rel "stylesheet" :href "/css/table.css"}]
    [:link {:rel "stylesheet" :href "/css/hightlight.css"}]]
   [:body
    [:header
     [:div.logo
      [:a {:href "/"} "Put some ink into the inkpot"]]
     [:nav
      [:a {:href "/"} "Home"]
      [:a {:href "/archive.html"} "Archive"]
      [:a {:href "/atom.xml"} "RSS"]]]
    [:main
     (if embed embed "$body$")]
    [:footer "Site generated by pandoc and babashka"]]])