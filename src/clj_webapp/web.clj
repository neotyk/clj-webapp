(ns clj-webapp.web
  (:require [ring.adapter.jetty :as jetty]
            [compojure.handler :as handler]
            [ring.middleware.file :as file]
            [ring.middleware.file-info :as file-info]
            [swank.swank :as swank]
            [clojure.tools.logging :as log]))

(def *SERVER* (ref nil))

(def app (handler/site (-> identity
                           (file/wrap-file "static")
                           file-info/wrap-file-info)))

(defn start-server []
  (log/info (str "Starting server on port #8040"))
  (let [s (jetty/run-jetty #'app {:port 8040
                                  :join? false})]
    (dosync
     (ref-set *SERVER* s)))
  (swank/start-repl 4005))

(defn stop-server []
  (when @*SERVER*
    (log/info "Stopping server")
    (.stop @*SERVER*)
    (dosync
     (ref-set *SERVER* nil))))

(defn -main []
  (log/info "Main start.")
  (start-server))
