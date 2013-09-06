(ns guestbook.handler
  (:use compojure.core
        ring.middleware.resource
        ring.middleware.file-info
        hiccup.middleware
        guestbook.routes.home)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [guestbook.models.db :as db]
            [guestbook.routes.auth :refer [auth-routes]]
            [noir.session :as session]
            [ring.middleware.session.memory :refer [memory-store]]
            [noir.validation :refer [wrap-noir-validation]]))

;START:handler-init
(defn init []
  (println "guestbook is starting")
  (if-not (.exists (java.io.File. "./db.sq3"))
    (db/create-guestbook-table)))
;END:handler-init

(defn destroy []
  (println "guestbook is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))


(def app
  (->
    (handler/site
      (routes auth-routes
              home-routes
              app-routes))
    (session/wrap-noir-session
      {:store (memory-store)})
    (wrap-noir-validation)))

(def war-handler
  (-> app
    (wrap-resource "public")
    (wrap-base-url)
    (wrap-file-info)))




