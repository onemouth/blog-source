(ns template.revealjs)

(defn template []
  [:html
   [:head
    [:meta {:charset "utf-8"}]
    [:link {:rel "stylesheet"
            :href "https://cdnjs.cloudflare.com/ajax/libs/reveal.js/3.6.0/css/reveal.min.css"
            :integrity "sha512-V5fKCVKOFy36w8zJmLzPH5R6zU6KvuHOvxfMRczx2ZeqTjKRGSBO9yiZjCKEJS3n6EmENwrH/xvSwXqxje+VVA=="
            :crossorigin "anonymous"
            :referrerpolicy "no-referrer"}]
    [:link {:rel "stylesheet"
            :href "https://cdnjs.cloudflare.com/ajax/libs/reveal.js/3.6.0/css/theme/white.min.css"
            :integrity "sha512-0BwgQfciYCCQc2rKINa7wL2k5vGAjF5dyL90A598QMimsVDie6M/4ShOLG07HJpNtSc9JB6m2s9B9OGam3rmTg=="
            :crossorigin "anonymous"
            :referrerpolicy "no-referrer"}]]
   [:body
    [:div.reveal
     [:div.slides
      "$body$"]]]
   [:script {:src "https://cdnjs.cloudflare.com/ajax/libs/reveal.js/3.6.0/js/reveal.min.js"
             :integrity "sha512-QYXU3Cojl94ZRiZRjUZpyg1odj9mKTON9MsTMzGNx/L3JqvMA3BQNraZwsZ83UeisO+QMVfFa83SyuYYJzR9hw=="
             :crossorigin "anonymous"
             :referrerpolicy "no-referrer"}]
   [:script {:src "https://cdnjs.cloudflare.com/ajax/libs/reveal.js/3.6.0/plugin/math/math.min.js"
             :integrity "sha512-Ff14h74LQq8X4XUGJ1yA4MYyVxtT1yu/Eo4cCpuPq1SN9DyW+kt6PXbgDz70PGZ95o87Sxkg6cTMO5bSxxJWsQ=="
             :crossorigin "anonymous"
             :referrerpolicy "no-referrer"}]
   [:script "Reveal.initialize();"]])