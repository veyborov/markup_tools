(ns classification_checker.services
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [classification_checker.dispatcher :as dispatcher]
    [cljs-http.client :as http]
    [classification_checker.example :as example]
    [cljs.core.async :refer [<!]]))

(defn redirect! [loc] (set! (.-location js/window) loc))

(defn process-response! [response ok-callback]
  (cond
    (<= 200 (:status response) 299) (ok-callback)
    (= (:status response) 403) (dispatcher/emit :login-needed nil)
    :else (binding [*print-fn* *print-err-fn*] (println (str "error code " (:status response))))))

(defn create-session! [user]
  (go (let [response (<! (http/post "/session/new" {:with-credentials? false :json-params {:user (clj->js user)}}))]
        (process-response! response (fn [] (redirect! "/paraphrase/current"))))))

(defn download-batch! []
  (go (let [
            response (<! (http/get "/batch" {:with-credentials? false}))
            batch (:batch (js->clj(:body response)))
            examples (into (hash-map) (map (fn [ex] (let [
                                                                 example (example/paraphrase-example ex)]
                                                   {(example/id example) example} )) batch))
            ]
        (process-response! response (fn [] (dispatcher/emit :downloaded examples))))))

(defn upload-batch! [checked-tasks callback] (go (let [
                                 response (<! (http/post "/batch" {:with-credentials? false :json-params {:batch checked-tasks}}))]
                             (process-response! response callback))))

