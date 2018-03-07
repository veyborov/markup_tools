(ns classification_checker.store
  (:require [reagent.core :as reagent]
            [classification_checker.dispatcher :as dispatcher]
            [classification_checker.example :as example]
            [classification_checker.util :as util]))

(def unchecked-tasks (reagent/atom '[]))
(def checked-tasks (atom '[]))

(defn download-batch! []
  (let [batch [{:id "1" :class "c1" :value "v1"} {:id "2" :class "c2" :value "v2"} {:id "3" :class "c3" :value "v3"}]]
    (swap! unchecked-tasks concat batch) ))

(defn upload-batch! [] (reset! checked-tasks '[])) ;TODO

(defn check! [is-right ex]
  (println (example/class ex) (example/value ex) is-right)
  (swap! unchecked-tasks (partial remove #(= (example/id ex) (example/id %))))
  (swap! checked-tasks conj (merge { :is-right is-right } ex))
  (if (empty? @unchecked-tasks) (do (upload-batch!) (download-batch!)) ))

(dispatcher/register :marked-right (partial check! example/right))
(dispatcher/register :marked-wrong (partial check! example/wrong))
(dispatcher/register :skiped (partial check! example/unknown))
(download-batch!)
