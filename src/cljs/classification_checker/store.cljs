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

(defn login! []






        (go (let [response (<! (http/post "/login" {:with-credentials? false :json-params {:email (email values)}}))]
            (if (= (:status response) 200) (redirect! "/check-markup")
              ;TODO
              )))

  (redirect! "/login"))

(defn download-batch! []
  (go (let [
             response (<! (http/get "/batch" {:with-credentials? false}))
             batch (js->clj(:body response))] ;TODO
        (if (= (:status response) 200)
          (reset! unchecked-tasks batch)
          (dispatcher/emit :login-needed nil)))))

(defn upload-batch! [] (go (let [
                                  response (<! (http/post "/batch" {:with-credentials? false :json-params {:batch @checked-tasks}}))]
                             (if (= (:status response) 202)
                               (reset! checked-tasks '[])
                               (dispatcher/emit :login-needed nil)))))

(dispatcher/register :next-page-selected (fn []
                                           (if (empty? @unchecked-tasks) (do (upload-batch!) (download-batch!)))
                                           (if-let [
                                                          id (example/id (first @unchecked-tasks))
                                                          example (get @unchecked-tasks id)]
                                               (do
                                                 (redirect! (str "/paraphrase/" id))
                                                 (reset! current-task example)))))

(defn check! [setter id] (let [
                                task (get @unchecked-tasks id)
                                checked-task (setter task "" "")]
                           (swap! checked-task conj checked-task)
                           (swap! unchecked-tasks #(dissoc % id))
                           (dispatcher/emit :next-page-selected nil)))

(dispatcher/register :marked-right (fn [id] (check! example/right id) ))
(dispatcher/register :marked-wrong (fn [id] (check! example/wrong id) ))
(dispatcher/register :skipped (fn [id]
                                (let [
                                       task (get @unchecked-tasks id)]
                                  (swap! unchecked-tasks #(dissoc % id))
                                  (dispatcher/emit :next-page-selected nil))))

(dispatcher/register :login-needed (fn [_] (if (= (.-location js/window) "/new-session")
                                             (redirect! "/new-session"))))

(dispatcher/register :email-received (fn [id] (check! example/right id) ))
