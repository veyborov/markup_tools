(ns classification_checker.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :include-macros true]
            [classification_checker.view :as main]
            [antizer.reagent :as ant]
            [classification_checker.store :as store]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [classification_checker.dispatcher :as action]
            [accountant.core :as accountant]))

(enable-console-print!)
;; -------------------------
;; Views

(defn redirect! [loc] (set! (.-hash js/window) loc))

(defn check-markup-page [] (if (= "unknown" @store/current-user)
                             (redirect! "/login")
                             (main/component "Примеры значат одно и то же?" @store/unchecked-tasks)))

(defn about-page []
  [:div [:h2 "About classification_checker"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn login-page []
  (defn submit-form-if-valid [errors values]
    (def email (second (first (-> (fn [result key]
                                    (let [v (aget values key)]
                                      (if (= "function" (goog/typeOf v)) result (assoc result key v))))
                                  (reduce {} (.getKeys goog/object values))))))
    (if (nil? errors)
      (go (let [response (<! (http/post "/login" {:with-credentials? false :json-params {:email email }}))]
            (if (= (:status response) 200)
              (do
                (println email)
                (defn click-right [] (action/emit :logged-in email))
                (redirect! "#check-markup"))
              ;TODO
              )))
      ))

  (ant/create-form (fn [props] (let [form (ant/get-form) submit-handler #(ant/validate-fields form submit-form-if-valid)]
                                 [:div {:style { :display "flex" :align-items "center" :justify-content "center" :height "100%"} }
                                  [ant/form {:layout "horizontal" :on-submit #(do (.preventDefault %) (submit-handler))}
                                   [ant/form-item {:label "Email"}
                                    (ant/decorate-field form "email" {:rules [{:required true} {:type "email"}]} [ant/input])]
                                   [ant/form-item
                                    [:div {:style {:text-align "center"}}
                                     [ant/button {:type "primary" :html-type "submit"} "ok"] ]]]]))))


;; -------------------------
;; Routes

(defonce page (atom #'login-page))

(defn current-page [] @page)

(secretary/defroute "/" [] (reset! page #'login-page))
(secretary/defroute "#login" [] (reset! page #'login-page))
(secretary/defroute "#check-markup" [] (reset! page #'check-markup-page))
(secretary/defroute "#about" [] (reset! page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))

