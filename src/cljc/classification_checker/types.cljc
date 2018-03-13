(ns classification_checker.types)

;(defn id [example] (:id example))
;(defn class [example] (:class example))
;(defn value [example] (:value example))
;(defn is-right? [example] (:is-right example))
;(defn is-right [example is-right] (merge { :is-right is-right } example))
;
(def right true)
(def wrong false)
(def unknown nil)


(defn example-from-class-value [class value] ( {:class class :value value :is-right? nil :assessor nil }))
(defn example-right [assessor example] (conj example {:is-right? true :assessor assessor}))
(defn example-wrong [assessor example] (conj example {:is-right? false :assessor assessor}))
(defn example-unknown [assessor example] (conj example {:is-right? nil}))
(defn example-equals [ex1 ex2]  (.equals ex1 ex2))
