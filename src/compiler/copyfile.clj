(ns compiler.copyfile
  (:require [clojure.java.io :as io]))


(defn copyfile [content dest]
  (spit (io/file dest) content))