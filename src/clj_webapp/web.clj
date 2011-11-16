(ns clj-webapp.web
  (:require [ring.adapter.jetty :as jetty]
            [compojure.handler :as handler]
            [ring.middleware.file :as file]
            [ring.middleware.file-info :as file-info]
            [swank.swank :as swank]
            [clojure.tools.logging :as log]))

(def *SERVER* (ref nil))

(defn wrap-logging
  "Wraps with request logging."
  [handler]
  (fn [{:keys [remote-addr request-method uri] :as request}]
    (log/info (format "[IP: %s] %s %s" remote-addr request-method uri))
    (try
      (let [start (.getTime (java.util.Date.))
            response (handler request)
            time (- (.getTime (java.util.Date.))
                    start)]
        (log/info (format "[IP: %s] %s %s, Result code: %d in: %d ms."
                          remote-addr request-method uri (:status response) time))
        response)
      (catch Exception e
        (log/warn (str "Failed request: " (format "[IP: %s] %s %s" remote-addr request-method uri) ", error: " (.getMessage e)))
        (throw e)))))

(def app (handler/site (-> identity
                           (file/wrap-file "static")
                           file-info/wrap-file-info
                           wrap-logging)))

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
