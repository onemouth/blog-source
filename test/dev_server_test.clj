(ns dev-server-test
  (:require [clojure.test :refer [deftest is run-tests]]
            [babashka.fs :as fs]
            [dev-server :as server]))

(defn with-site [f]
  (let [dir (fs/create-temp-dir {:prefix "blog-server-test-"})
        site (fs/path dir "_site")
        files {"index.html" "Homepage"
               "archive.html" "Archive"
               "posts/2026-10-11-buglar-brown.html" "Article"
               "posts/a+b.html" "Plus"
               "posts/中文.html" "Chinese"
               "css/default.css" "body {}"
               "atom.xml" "<feed/>"
               "robots.txt" "User-agent: *"
               "nested/index.html" "Nested"
               "exact" "Direct file"}]
    (try
      (doseq [[name content] files]
        (let [path (fs/path site name)]
          (fs/create-dirs (fs/parent path))
          (spit (str path) content)))
      (spit (str (fs/path dir "secret.html")) "Secret")
      (fs/create-sym-link (fs/path site "escape.html") (fs/path dir "secret.html"))
      (f (server/handler site))
      (finally (fs/delete-tree dir)))))

(deftest page-and-asset-routing
  (with-site
    (fn [handler]
      (doseq [[uri expected] [["/" "Homepage"]
                              ["/archive" "Archive"]
                              ["/archive.html" "Archive"]
                              ["/posts/2026-10-11-buglar-brown" "Article"]
                              ["/posts/2026-10-11-buglar-brown.html" "Article"]
                              ["/posts/a+b" "Plus"]
                              ["/posts/%E4%B8%AD%E6%96%87" "Chinese"]
                              ["/css/default.css" "body {}"]
                              ["/atom.xml" "<feed/>"]
                              ["/robots.txt" "User-agent: *"]
                              ["/nested/" "Nested"]
                              ["/exact" "Direct file"]]]
        (let [response (handler {:uri uri :request-method :get})]
          (is (= 200 (:status response)) uri)
          (is (= expected (slurp (:body response))) uri)
          (is (= "no-store" (get-in response [:headers "Cache-Control"])) uri)))
      (doseq [[uri mime] [["/archive" "text/html"]
                         ["/css/default.css" "text/css"]
                         ["/atom.xml" "text/xml"]
                         ["/robots.txt" "text/plain"]]]
        (is (= mime (get-in (handler {:uri uri :request-method :get})
                            [:headers "Content-Type"])) uri)))))

(deftest missing-and-unsafe-paths
  (with-site
    (fn [handler]
      (doseq [uri ["/missing" "/posts/missing" "/css/missing.css"
                   "/../secret.html" "/%2e%2e/secret.html"
                   "/posts/../../secret.html" "/escape.html"
                   "/%" "/%00" "/posts/"]]
        (is (= 404 (:status (handler {:uri uri :request-method :get}))) uri)))))

(deftest http-methods
  (with-site
    (fn [handler]
      (let [head (handler {:uri "/archive" :request-method :head})]
        (is (= 200 (:status head)))
        (is (= "text/html" (get-in head [:headers "Content-Type"])))
        (is (nil? (:body head))))
      (is (= 404 (:status (handler {:uri "/missing" :request-method :head}))))
      (let [post (handler {:uri "/archive" :request-method :post})]
        (is (= 405 (:status post)))
        (is (= "GET, HEAD" (get-in post [:headers "Allow"])))))))

(let [{:keys [fail error]} (run-tests 'dev-server-test)]
  (when (pos? (+ fail error))
    (System/exit 1)))
