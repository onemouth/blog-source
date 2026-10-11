(ns dev-server
  (:require [babashka.fs :as fs]
            [babashka.http-server :as static]
            [clojure.string :as string]
            [org.httpkit.server :as http])
  (:import [java.net URLDecoder]))

(defn- public-file? [root path]
  (and (fs/regular-file? path)
       (fs/readable? path)
       ;; Resolve symlinks too: never serve files outside the output directory.
       (.startsWith (fs/real-path path) root)))

(defn handler [dir]
  (let [root (fs/real-path dir)]
    (fn [{:keys [uri request-method]}]
      (if-not (#{:get :head} request-method)
        {:status 405 :headers {"Allow" "GET, HEAD"} :body "Method not allowed"}
        (let [file (try
                     ;; URLDecoder treats '+' as a space; in URL paths it is literal.
                     (let [decoded (URLDecoder/decode (string/replace uri "+" "%2B") "UTF-8")
                           path (-> (fs/path root (string/replace-first decoded #"^/" ""))
                                    fs/normalize)]
                       (when (.startsWith path root)
                         (some #(when (public-file? root %) %)
                               [path (fs/path path "index.html")
                                (fs/path (str path ".html"))])))
                     (catch Exception _ nil))]
          (if file
            (let [extension (some-> (fs/file-name file)
                                    (string/split #"\.")
                                    last
                                    string/lower-case)
                  mime (get static/default-mime-types extension "application/octet-stream")]
              {:status 200
               :headers {"Content-Type" mime
                         "Cache-Control" "no-store"}
               :body (when (= :get request-method) (fs/file file))})
            {:status 404 :body "Not found"}))))))

(defn serve [{:keys [dir port ip]
              :or {dir "_site" port 8002 ip "127.0.0.1"}}]
  (when-not (fs/directory? dir)
    (throw (ex-info (str "Missing output directory " dir "; run bb build first.")
                    {:dir dir})))
  (let [stop (http/run-server (handler dir) {:port port :ip ip})]
    (println (str "Serving " dir " at http://" ip ":" port
                  " (extensionless HTML links supported)"))
    stop))

(defn exec [opts]
  (if (:help opts)
    (println "Serve the built site: bb serve [--port 8002] [--dir _site]. Run bb build first.")
    (do
      (serve opts)
      @(promise))))
