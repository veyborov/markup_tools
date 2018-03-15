(ns classification_checker.dispatcher
  (:require-macros
    [cljs.core.async.macros :refer [go-loop]])
  (:require
    [cljs.core.async :refer [chan put! <! >!]]))

(def actions (atom {}))

(defn register [action callback] (swap! actions conj {action callback}))

(defonce event-queue (chan))

(go-loop []
  (if-let [
            {action :action data :payload} (<! event-queue)
            callback (get @actions action)]
    (callback data)
    (recur)))

(defn emit [action payload] (put! actions {:action action :payload payload}))
