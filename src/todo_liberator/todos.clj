(ns todo-liberator.todos)

(def todo-store (atom {}))

(defn list []
    @todo-store)

(defn create [todo]
  (let [uuid (str (java.util.UUID/randomUUID))
        todo (assoc todo :id uuid)]
    (swap! todo-store assoc (keyword uuid) todo)))

(defn show [id]
  ((keyword id) @todo-store))

(defn change [id partial-todo]
  (swap! todo-store (fn [store]
                      (update-in store [(keyword id)] #(merge % partial-todo)))))

(defn delete [id]
  (swap! todo-store (fn [store]
                      (dissoc store (keyword id)))))

(defn delete-all []
  (reset! todo-store {}))


