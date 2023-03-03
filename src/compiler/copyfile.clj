(ns compiler.copyfile
  (:require [clojure.java.io :as io]))


(defn run [dest content]
  (io/make-parents dest)
  (spit (io/file dest) content :make-parents true)
  dest)