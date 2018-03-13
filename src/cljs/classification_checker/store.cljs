(ns classification_checker.store
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent]
            [classification_checker.dispatcher :as dispatcher]
            [classification_checker.types :refer [example-right example-wrong example-unknown example-from-class-value example-equals]]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(defonce current-user (reagent/atom "unknown"))
(defonce unchecked-tasks (reagent/atom '[]))
(defonce checked-tasks (atom '[]))

(defn download-batch! []
  (defn go-to-login! []
    (defn redirect! [loc] (set! (.-location js/window) loc))
    (redirect! "/login"))
  (go (let [response (<! (http/get "/batch" {:with-credentials? false}))]
        (if (= (:status response) 200)
          (defn input->example [ex] (example-from-class-value (:class ex) (:value ex)))
          (let [resp (js->clj (:body response))
                ; batch (map input->example resp)
                ]
            (swap! unchecked-tasks conj resp))
          (go-to-login!)))))

(defn upload-batch! [] (go (let [response (<! (http/post "/batch" {:with-credentials? false :json-params @checked-tasks}))]
                             (if (= (:status response) 202) (reset! checked-tasks '[]) (go-to-login!)) )))

(defn check! [setter ex]
  (swap! unchecked-tasks (partial remove #(example-equals ex %)))
  (swap! checked-tasks conj (setter @current-user ex))
  (if (empty? @unchecked-tasks) (do (upload-batch!) (download-batch!)) ))

(dispatcher/register :marked-right #(check! example-right %))
(dispatcher/register :marked-wrong #(check! example-wrong %))
(dispatcher/register :skipped #(check! example-unknown %))
(dispatcher/register :logged-in #((do (reset! current-user %) (download-batch!))))

