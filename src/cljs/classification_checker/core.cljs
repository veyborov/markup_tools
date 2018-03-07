(ns classification_checker.core
    (:require [reagent.core :as reagent :refer [atom]]
              [secretary.core :as secretary :include-macros true]
              [classification_checker.classification_example_view :as main]
              [antizer.reagent :as ant]
              [keybind.core :as key]

              [accountant.core :as accountant]))

;; -------------------------
;; Views

(defn home-page [] (main/component "жопа" "не жопа"))

(defn about-page []
  [:div [:h2 "About classification_checker"]
   [:div [:a {:href "/"} "go to the home page"]]])

;; -------------------------
;; Routes

(defonce page (atom #'home-page))

(defn current-page [] @page)

(secretary/defroute "/" []
  (reset! page #'home-page))

(secretary/defroute "/about" []
  (reset! page #'about-page))

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
