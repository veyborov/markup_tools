(ns classification-checker.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [classification_checker.middleware :refer [wrap-middleware]]
            [config.core :refer [env]]))

(def mount-target [:div#app])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "https://cdnjs.cloudflare.com/ajax/libs/antd/3.2.3/antd.css" "https://cdnjs.cloudflare.com/ajax/libs/antd/3.2.3/antd.min.css"))
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
    (head)
    [:body 
     mount-target
     (include-js "/js/app.js")]))


(defroutes routes
  (GET "/" [] (loading-page))
  (GET "/about" [] (loading-page))
  
  (resources "/")
  (not-found "Not Found"))

(def app (wrap-middleware #'routes))
