(ns compiler.cp
  (:require [clojure.java.io :as io]))


(defn copy-content [dest content]
  (io/make-parents dest)
  (spit (io/file dest) content)
  dest)

(defn copy-file [dest src]
  (io/make-parents dest)
  (with-open [in (io/input-stream src)
              out (io/output-stream dest)]
    (io/copy in out))
  dest)