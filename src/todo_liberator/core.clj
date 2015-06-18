(ns todo-liberator.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [clojure.data.json :as json]
            [todo-liberator.todos :as todos]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [compojure.handler :refer [site]]
            [compojure.core :refer [defroutes ANY]]))

(defn body-in [context]
 (json/read-str (slurp (get-in context [:request :body]))))

(defresource all-todos
  :available-media-types ["application/json"]
  :allowed-methods [:get :post :delete]
  :post! (fn [context] (todos/create (body-in context)))
  :delete! (fn [_] (todos/delete-all))
  :handle-ok (fn [_] (todos/list)))

(defresource todos [id]
  :available-media-types ["application/json"]
  :allowed-methods [:get :patch :delete]
  :patch! (fn [context] (todos/change id (body-in context)))
  :delete! (fn [_] (todos/delete id))
  :handle-ok (fn [_] (todos/show id)))

(defroutes app
  (ANY "/todos" [] all-todos)
  (ANY "/todos/:id" [id] (todos id)))

(def handler
  (-> app
      wrap-params))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'handler) {:port port :join? false})))
