(ns site-test
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [cheshire.core :as json]
            [babashka.process :refer [sh]]
            [site :as site]))

(def post
  {:title "測試文章"
   :summary "文章摘要"
   :url "/posts/2026-07-10-test"
   :lang "zh-Hant"
   :date-obj "2026-07-10"
   :author "LT"})

(defn schema [metadata type]
  (json/parse-string (:json-ld (site/page-meta metadata type))))

(deftest article-schema
  (let [data (schema post "article")]
    (is (= "BlogPosting" (get data "@type")))
    (is (= "https://schema.org" (get data "@context")))
    (is (= (:title post) (get data "headline")))
    (is (= (:summary post) (get data "description")))
    (is (= "LT" (get-in data ["author" "name"])))
    (is (= "Person" (get-in data ["author" "@type"])))
    (is (= "zh-Hant" (get data "inLanguage")))
    (is (= "2026-07-10" (get data "datePublished")))
    (is (= (site/absolute-url (:url post)) (get data "url")))
    (is (= (get data "url") (get-in data ["mainEntityOfPage" "@id"])))
    (is (not (contains? data "dateModified")))
    (is (not (contains? data "image")))))

(deftest defaults-and-static-pages
  (testing "future posts reuse metadata without separately authored JSON"
    (let [data (schema (dissoc post :author :lang :summary) "article")]
      (is (= (:author-name site/config) (get-in data ["author" "name"])))
      (is (= (:default-lang site/config) (get data "inLanguage")))
      (is (= (:title post) (get data "description")))))
  (testing "homepage describes the website, not the Home page title"
    (let [data (schema {:title "LT's blog" :summary "About this blog"
                        :url "/" :lang "en"} "website")]
      (is (= "WebSite" (get data "@type")))
      (is (= (:title site/config) (get data "name")))
      (is (= (site/absolute-url "/") (get data "url")))
      (is (= "en" (get data "inLanguage")))
      (is (not (contains? data "datePublished")))))
  (testing "archive does not duplicate homepage WebSite schema"
    (is (nil? (:json-ld (site/page-meta {:title "Archive" :url "/archive"}
                                      "website"))))))

(deftest safe-json-through-pandoc
  (let [text "中文 \"quotes\" & <em>literal</em> </script><script>alert('x')</script> $body$"
        metadata (assoc post :title text :summary text)
        page (site/page-meta metadata "article")
        result (apply sh {:in ""}
                      (concat ["pandoc" "-f" "markdown" "--template=templates/post.html"]
                              (site/template-args page)))
        html (:out result)
        blocks (re-seq #"(?s)<script type=\"application/ld\+json\">(.*?)</script>" html)
        data (json/parse-string (second (first blocks)))]
    (is (zero? (:exit result)))
    (is (= 1 (count blocks)))
    (is (= text (get data "headline")))
    (is (= text (get data "description")))
    (is (not (.contains (:json-ld page) "<")))
    (is (not (.contains html "<script>alert('x')</script>")))
    (is (.contains (:json-ld page) "\\u003c"))))

(let [{:keys [fail error]} (run-tests 'site-test)]
  (when (pos? (+ fail error))
    (System/exit 1)))
