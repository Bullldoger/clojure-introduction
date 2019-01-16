
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

(def taxi-park (read-file "src/skoltaxi.edn"))
(def current-taxi (atom nil))

(defn get-taxi-car [id]
  (loop [counter (dec (count taxi-park))]
    (let [taxi (nth taxi-park counter) taxi-id (get @taxi :id)]
      (if (not (= counter 0))
        (if (= id taxi-id) taxi (recur (dec counter)))
        nil))))

(defn generate-id []
  (loop [ids (set '()) counter (dec (count taxi-park))]
    (if (> counter 0) (do (let [current-id (get (nth taxi-park counter) :id)]
                            (recur (conj ids current-id) (dec counter))))
        (loop [new-id (rand-int 100)] (if (contains? ids new-id) (do (println new-id) (recur (rand-int 100))) new-id)))))

(defn create-taxi [& {:keys [passengers from to time cost messages]
                      :or {passengers 0, from nil, to nil, time nil, cost nil, messages []}}]
  (let [taxi
        {:id (generate-id)
         :passengers passengers
         :from from
         :to to
         :time time
         :cost cost
         :messages messages}] (conj taxi-park taxi), (reset! current-taxi taxi), taxi))

(defn post-message [message & {:keys [taxi] :or {taxi @current-taxi}}]
    (let [updated-taxi (apply-patch {:type :post :path [:messages] :value message} taxi)] (reset! current-taxi updated-taxi))
  )

(defn join-taxi []
    (reset! current-taxi (update-in @current-taxi [:passengers] inc))
)

(defn set-current-taxi [id] (do (reset! current-taxi @(get-taxi-car id))))

(defn -main []
  (do
    (create-taxi)
    (println @current-taxi)
    (set-current-taxi 1)
    (println @current-taxi)
    (join-taxi)
    (post-message "asdf")
    (println @current-taxi))
)