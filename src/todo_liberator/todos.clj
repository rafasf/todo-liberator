(ns todo-liberator.todos)

(def todo-store (atom {}))

(defn list []
    @todo-store)

(defn store [todo]
  (swap! todo-store assoc (todo "id") todo))

(defn create [todo]
  (let [uuid (str (java.util.UUID/randomUUID))
        new-todo (assoc todo "id" uuid "completed" false)]
    (store new-todo)
    new-todo))

(defn show [id]
  (@todo-store id))

(defn change [id partial-todo]
  (let [updated (update-in @todo-store [id] #(merge % partial-todo))]
    (reset! todo-store updated)
    (@todo-store id)))

(defn delete [id]
  (swap! todo-store dissoc id))

(defn delete-all []
  (reset! todo-store {}))

