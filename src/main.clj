#!/usr/bin/env bb


(defn -main [& args]
  (println args))


(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))