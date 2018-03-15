(ns classification_checker.store
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent]
            [classification_checker.dispatcher :as dispatcher]
            [cljs-http.client :as http]
            [classification_checker.example :as example]
            [cljs.core.async :refer [<!]]))

(def unchecked-tasks (atom {}))
(def checked-tasks (atom []))
(def nilExample (example/paraphrase-example {:utterance1 nil :utterance2 nil}))
(def current-task (reagent/atom nilExample))

(defn redirect! [loc] (set! (.-location js/window) loc))

(defn process-response! [response ok-callback] (cond
                                      (= (:status response) 200) (ok-callback)
                                      (= (:status response) 403) (dispatcher/emit :login-needed nil)
                                                :else (binding [*out* *err*] (println (str "error code " (:status response))))))

(defn create-session! [user]
  (go (let [response (<! (http/post "/session/new" {:with-credentials? false :json-params {:user (clj->js user)}}))]
        (process-response! response (fn [] (redirect! "/paraphrase/current"))))))

(defn download-batch! []
  (go (let [
             response (<! (http/get "/batch" {:with-credentials? false}))
             batch (:batch (js->clj(:body response)))
             examples (into (hash-map) (map batch (fn [ex] {(example/id ex) ex} )))]
        (process-response! response (fn [] (reset! unchecked-tasks examples))))))

(defn upload-batch! [] (go (let [
                                  response (<! (http/post "/batch" {:with-credentials? false :json-params {:batch @checked-tasks}}))]
                             (process-response! response (fn [] (reset! checked-tasks '[]))))))

(defn next-page! []
      (if (empty? @unchecked-tasks) (do (upload-batch!) (download-batch!)))
      (if-let [
                id (example/id (first @unchecked-tasks))
                example (get @unchecked-tasks id)]
        (reset! current-task example)))

(defn check! [setter id] (let [
                                task (get @unchecked-tasks id)
                                checked-task (setter task "" "")]
                           (swap! checked-task conj checked-task)
                           (swap! unchecked-tasks #(dissoc % id))
                           (next-page!)))

(dispatcher/register :marked-right (fn [id] (check! example/right id) ))
(dispatcher/register :marked-wrong (fn [id] (check! example/wrong id) ))
(dispatcher/register :skipped (fn [id]
                                (let [
                                       task (get @unchecked-tasks id)]
                                  (swap! unchecked-tasks #(dissoc % id))
                                  (next-page!))))

(dispatcher/register :login-needed (fn [_] (if (= (.-location js/window) "/session/new")
                                             (redirect! "/session/new"))))

(dispatcher/register :email-received (fn [user] (create-session! user)))
