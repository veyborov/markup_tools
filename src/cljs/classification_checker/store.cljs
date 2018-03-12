(ns classification_checker.store
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent]
            [classification_checker.dispatcher :as dispatcher]
            [classification_checker.example :as example]
            [classification_checker.core :refer [go-to-login!]]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [classification_checker.util :as util]))

(def unchecked-tasks (reagent/atom '[]))
(def checked-tasks (atom '[]))

(enable-console-print!)

(defn download-batch! []
  (if (empty? @unchecked-tasks)
    (go (let [response (<! (http/get "/batch" {:with-credentials? false}))]
          (if (= (:status response) 200)
            (let [batch (js->clj(:body response))] (reset! unchecked-tasks batch))
            (go-to-login!))))))

(defn upload-batch! [] (go (let [response (<! (http/post "/batch" {:with-credentials? false :json-params @checked-tasks}))]
                             (if (= (:status response) 202) (reset! checked-tasks '[]) (go-to-login!)) )))

(defn check! [is-right? ex]
  (swap! unchecked-tasks (partial remove #(= (example/id ex) (example/id %))))
  (swap! checked-tasks conj (example/is-right ex is-right?))
  (if (empty? @unchecked-tasks) (do (upload-batch!) (download-batch!)) ))

(dispatcher/register :marked-right (partial check! example/right))
(dispatcher/register :marked-wrong (partial check! example/wrong))
(dispatcher/register :skiped (partial check! example/unknown))
