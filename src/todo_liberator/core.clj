(ns todo-liberator.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [clojure.data.json :as json]
            [todo-liberator.todos :as todos]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.cors :refer [wrap-cors]]
            [environ.core :refer [env]]
            [compojure.handler :refer [site]]
            [compojure.core :refer [routes defroutes ANY OPTIONS]]))

(def base-url "http://shrouded-stream-3826.herokuapp.com")

(defn body-in [context]
 (json/read-str (slurp (get-in context [:request :body]))))

(defn todo->representation [todo]
  (cond
    (nil? todo) {}
    :else (assoc todo "url" (str base-url "/todos/" (todo "id")))))

(defresource all-todos
  :available-media-types ["application/json"]
  :allowed-methods [:get :post :delete]

  :post! (fn [context]
           (let [newest (todos/create (body-in context))]
             {::newest newest}))
  :delete! (fn [_]
             (todos/delete-all))

  :handle-ok (fn [_]
               (-> (vals (todos/list))
                   (#(map todo->representation %))))
  :handle-created (fn [context]
                    (-> (::newest context)
                        todo->representation)))

(defresource todos [id]
  :available-media-types ["application/json"]
  :allowed-methods [:get :patch :delete]
  :respond-with-entity? true

  :patch! (fn [context]
            (let [updated (todos/change id (body-in context))]
              {::entity updated}))
  :delete! (fn [_] (todos/delete id))

  :handle-ok (fn [_]
               (-> (todos/show id)
                   todo->representation)))

(defroutes todo-routes
  (OPTIONS "/todos" [] {:status 200})
  (OPTIONS "/todos/:id" [] {:status 200})
  (ANY "/todos" [] all-todos)
  (ANY "/todos/:id" [id] (todos id)))

(defn todo-cors [app]
  (wrap-cors app :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :put :post :delete :patch :options]))
(def app
  (-> (routes todo-routes)
      (wrap-params)
      (todo-cors)))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty app {:port port :join? false})))
