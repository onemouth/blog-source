(ns updated-date-test
  (:require [clojure.test :refer [deftest is run-tests]]
            [clojure.string :as string]
            [clojure.data.xml :as xml]
            [babashka.fs :as fs]
            [babashka.process :refer [sh]]
            [cheshire.core :as json]
            [compiler.pandoc :as pandoc]
            [template.sitemap :as sitemap]
            [main]
            [site :as site]))

(defn with-post [field f]
  (let [dir (fs/create-temp-dir {:prefix "blog-date-test-"})
        path (str (fs/path dir "2026-07-10-test.md"))]
    (try
      (spit path (str "---\ntitle: Test\nlang: zh-Hant\n" field "\n---\n\nBody.\n"))
      (f path)
      (finally (fs/delete-tree dir)))))

(defn render [metadata]
  (let [page (site/page-meta metadata "article")
        result (apply sh {:in ""}
                      (concat ["pandoc" "-f" "markdown" "--template=templates/post.html"]
                              (site/template-args page)))]
    (is (zero? (:exit result)))
    (:out result)))

(defn schema [html]
  (json/parse-string
   (second (re-find #"(?s)<script type=\"application/ld\+json\">(.*?)</script>" html))))

(defn og-modified [html]
  (some (fn [tag]
          (when (string/includes? tag "property=\"article:modified_time\"")
            (second (re-find #"content=\"([^\"]*)\"" tag))))
        (re-seq #"<meta[^>]*>" html)))

(deftest valid-date-input
  (doseq [field ["updated_date: 2026-07-12"
                 "updated_date: \"2026-07-12\""
                 "updated_date: '2026-07-12'"
                 "updated_date: 2026-07-12 # substantive revision"]]
    (with-post field
      (fn [path]
        (let [metadata (pandoc/parse-meta path)]
          (is (= "2026-07-12" (:updated_date metadata)))
          (is (= "2026-07-12" (str (:updated-date-obj metadata))))
          (is (= "2026-07-10" (str (:date-obj metadata))))))))
  (with-post "updated_date: 2026-07-10"
    #(is (= "2026-07-10" (str (:updated-date-obj (pandoc/parse-meta %))))))
  (with-post "updated_date: 2028-02-29"
    #(is (= "2028-02-29" (str (:updated-date-obj (pandoc/parse-meta %)))))))

(deftest invalid-date-input
  (doseq [value ["2027-02-30" "\"2027-02-30\"" "2027-02-29" "2026-13-01"
                 "2026-07-09" "2026-7-12" "2026/07/12" "\"July 12, 2026\""
                 "2026-07-12T00:00:00Z" "\"2026-07-12T12:00:00+08:00\""
                 "true" "20260712" "[2026-07-12]" "\"\""]]
    (with-post (str "updated_date: " value)
      (fn [path]
        (let [error (try (pandoc/parse-meta path) nil
                         (catch clojure.lang.ExceptionInfo e e))]
          (is (some? error) value)
          (is (= path (:path (ex-data error))) value)
          (is (= :updated_date (:field (ex-data error))) value))))))

(deftest omitted-date
  (doseq [field ["" "updated_date:" "updated_date: null"]]
    (with-post field
      (fn [path]
        (let [metadata (pandoc/parse-meta path)
              html (render metadata)
              data (schema html)
              sitemap (sitemap/sitemap-xml (:root site/config) [metadata])]
          (is (nil? (:updated-date-obj metadata)))
          (is (nil? (og-modified html)))
          (is (not (contains? data "dateModified")))
          (is (not (string/includes? sitemap "<lastmod>")))
          (is (= "2026-07-10" (get data "datePublished"))))))))

(deftest consistent-modification-outputs
  (with-post "updated_date: 2026-07-12"
    (fn [path]
      (let [metadata (pandoc/parse-meta path)
            html (render metadata)
            data (schema html)
            sitemap (sitemap/sitemap-xml (:root site/config)
                                          [metadata {:url "/posts/untouched"}])
            parsed (xml/parse-str sitemap)
            lastmods (filter #(and (map? %) (:tag %) (= "lastmod" (name (:tag %))))
                             (tree-seq coll? seq parsed))
            context (atom {:posts [(assoc metadata :dest
                                          "_site/posts/2023-09-24-jp-okayama-trip.html")]})
            entry (first (#'main/get-entries context))
            fields (into {} (map (juxt first second) (rest entry)))]
        (is (= "2026-07-12" (get data "dateModified")))
        (is (= "2026-07-12" (og-modified html)))
        (is (= 1 (count lastmods)))
        (is (= ["2026-07-12"] (:content (first lastmods))))
        (is (= "2026-07-10" (get data "datePublished")))
        (is (= "2026-07-10T12:00:00+08:00" (:published fields)))
        (is (= "2026-07-12T12:00:00+08:00" (:updated fields)))
        (is (= (str (site/absolute-url (:url metadata)) ".html") (:id fields)))))))

(let [{:keys [fail error]} (run-tests 'updated-date-test)]
  (when (pos? (+ fail error))
    (System/exit 1)))
