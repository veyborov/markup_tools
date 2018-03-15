(ns classification_checker.core
  (:require [classification_checker.views :refer [paraphrase-view identification-view]]
            [classification_checker.dispatcher :as dispatcher]
            [classification_checker.store :refer [current-task]]
            [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]))

(enable-console-print!)

;; -------------------------
;; Views

(defn check-markup-page [] (paraphrase-view "Примеры значат одно и то же?" @current-task))

(defn login-page [] (identification-view))

;; -------------------------
;; Routes

(defonce page (atom #'login-page))

(defn current-page [] @page)

(secretary/defroute "/" [] (set! (.-location js/window) "/session/new"))
(secretary/defroute "/session/new" [] (reset! page #'login-page))
(secretary/defroute "/paraphrase/current" [] (reset! page #'check-markup-page))

;; -------------------------
;; Initialize app

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (reagent/render [current-page] (.getElementById js/document "app")))

