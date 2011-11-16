(ns clj-webapp.core)

(def *STORE* (ref {}))

(defn get-store
  "Gets in memory store."
  []
  *STORE*)

(defn populate-initial-dataset
  "Creates initial dataset."
  []
  (dosync
   (alter *STORE* merge 
          {"1" {:id "1"
                :body "joker escaped arkham again"
                :isDone true}
           "2" {:id "2"
                :body "riddler sent riemann hypothesis"
                :isDone false}
           "3" {:id "3"
                :body "bane wants to meet, not worried"
                :isDone false}})))

(defn read-todos!
  "Reads all todos"
  [store]
  (reverse (into [] (for [[k v] @store]
                      (assoc v :id k)))))
