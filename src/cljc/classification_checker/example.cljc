(ns classification_checker.example)

(defn id [example] (:id example))
(defn class [example] (:class example))
(defn value [example] (:value example))
(defn is-right? [example] (:is-right example))
(defn is-right [example is-right] (merge { :is-right is-right } example))

(def right true)
(def wrong false)
(def unknown nil)