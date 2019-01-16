
(ns skoltaxi
  (:require [clojure.spec.alpha :as s])
  (:require [patcher.core :refer [apply-patch] :as p])
  )

(require '[clojure.java.io :as io] '[clojure.edn :as edn])

(def patch {:type :put :path [:some :keys] :value "A new value"})

(defn load-edn
  "Load edn from an io/reader source (filename or io/resource)."
  [source]
  (try
    (with-open [r (io/reader source)]
      (edn/read (java.io.PushbackReader. r)))

    (catch java.io.IOException e
      (printf "Couldn't open '%s': %s\n" source (.getMessage e)))
    (catch RuntimeException e
      (printf "Error parsing edn file '%s': %s\n" source (.getMessage e)))))

(defn read-file [file-name] (let [readed (load-edn file-name)]
    (loop [counter (dec (count readed)) taxi-list []]
        (if (not (< counter 0)) 
            (do
                (recur (dec counter) (conj taxi-list (atom (nth readed counter))))) taxi-list)
        )
))

(defn -main []
  (println (load-edn "src/skoltaxi.edn"))
)