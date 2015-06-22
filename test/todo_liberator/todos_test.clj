(ns todo-liberator.todos-test
  (:require [clojure.test :refer :all]
            [todo-liberator.todos :refer :all]))

(deftest creation-and-changes
  (delete-all)

  (let [todo (create { "title" "late test" })]

    (testing "assings an id"
      (is (not (nil? (todo "id")))))

    (testing "is not completed by default"
      (is (= (todo "completed") false)))

    (testing "saves given title"
      (is (= (todo "title") "late test")))

    (testing "updates given property"
      (let [updated (change (todo "id") { "title" "new title here" })]
        (is (= (updated "title") "new title here"))
        (is (= (get-in todo ["completed" "id"]) (get-in updated ["completed" "id"])))))
  ))

(deftest storage-operations
  (delete-all)

  (let [todo (create { "title" "might get done"})
        another-todo (create { "title" "will not get done" })]

    (testing "returns all created"
      (is (= (list) (assoc {}
                            (todo "id") todo
                            (another-todo "id") another-todo))))

    (testing "returns by id"
      (is (= (show (todo "id")) todo)))

    (testing "deletes by id"
      (delete (another-todo "id"))
      (is (= (list) (assoc {} (todo "id") todo))))

    ))
