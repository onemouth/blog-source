(ns compiler.copyfile
  (:require [clojure.java.io :as io]))


(defn run-content [dest content]
  (io/make-parents dest)
  (spit (io/file dest) content)
  dest)

(defn run-cp [dest src]
  (io/make-parents dest)
  (with-open [in (io/input-stream src)
              out (io/output-stream dest)]
    (io/copy in out))
  dest)