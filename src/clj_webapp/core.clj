(ns clj-webapp.core
  (:import (java.util UUID)))

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
          {"1" {:body "joker escaped arkham again"
                :isDone true}
           "2" {:body "riddler sent riemann hypothesis"
                :isDone false}
           "3" {:body "bane wants to meet, not worried"
                :isDone false}})))

(defn read-todos!
  "Reads all todos"
  [store]
  (reverse (into [] (for [[k v] @store]
                      (assoc v :id k)))))

(defn store-todo!
  "Store new todo and return id of it"
  [store body done?]
  (let [id (str (UUID/randomUUID))]
    (dosync
     (alter *STORE* assoc
            id {:body body :isDone done? }))
    id))

(defn read-todo!
  "Reads single todo from store"
  [store id]
  (when-let [m (get @store id)]
    (assoc m :id id)))
