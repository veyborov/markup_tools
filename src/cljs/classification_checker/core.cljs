(ns classification_checker.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :include-macros true]
            [classification_checker.view :as main]
            [antizer.reagent :as ant]
            [classification_checker.store :refer [unchecked-tasks download-batch!]]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [accountant.core :as accountant]))

(enable-console-print!)

;; -------------------------
;; Views

(defn check-markup-page [] (do
                             (download-batch!)
                             (main/component "Примеры значат одно и то же?" @unchecked-tasks)))

(defn about-page []
  [:div [:h2 "About classification_checker"]
   [:div [:a {:href "/"} "go to the home page"]]])


(defn redirect! [loc] (set! (.-location js/window) loc))

(defn login-page []
  (defn email [obj]
    (second (first (-> (fn [result key]
          (let [v (aget obj key)]
            (if (= "function" (goog/typeOf v)) result (assoc result key v))))
        (reduce {} (.getKeys goog/object obj))))))
  (defn submit-form-if-valid [errors values]
    (if (nil? errors)
      (go (let [response (<! (http/post "/login" {:with-credentials? false :json-params {:email (email values)}}))]
            (if (= (:status response) 200) (redirect! "/check-markup")
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
(secretary/defroute "/login" [] (reset! page #'login-page))
(secretary/defroute "/check-markup" [] (reset! page #'check-markup-page))
(secretary/defroute "/about" [] (reset! page #'about-page))

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

