(ns classification-checker.handler
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [ring.util.response :refer [response]]
            [ring.util.http-response :refer [accepted created ok see-other forbidden]]
            [ring.middleware.json :refer [wrap-json-response]]
            [classification_checker.middleware :refer [wrap-middleware]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json :refer [wrap-json-params]]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [config.core :refer [env]]))

(use 'ring.middleware.session.cookie)

(def mount-target [:div#app])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "https://cdnjs.cloudflare.com/ajax/libs/antd/3.2.3/antd.css" "https://cdnjs.cloudflare.com/ajax/libs/antd/3.2.3/antd.min.css"))
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn main-page []
  (html5
    (head)
    [:body
     mount-target
     (include-js "/js/app.js")]))

(defn if-login [session ok-response]
  (if (contains? session :email) (ok-response) (forbidden)))

(defn login! [email]
  (-> (see-other "/check-markup")
      (assoc-in [:session :email] email)))

(defn read-example []
  (with-open [rdr (io/reader *in*)]
    (first (csv/read-csv rdr))))

(defroutes routes
  (GET "/" [] (main-page))
  (GET "/check-markup" {session :session} (if-login session main-page))
  (GET "/about" [] (main-page))
  (GET "/login" [] (main-page))
  (POST "/login" [& req] (login! (:email req)))
  (GET "/batch" {session :session} (if-login session #(response [{:id "1" :class "c1" :value "v11"} {:id "2" :class "c2" :value "v2"} {:id "3" :class "c3" :value "v3"}] )))
  (POST "/batch" {session :session} (if-login session #(do
                                       (prn session)
                                       (accepted))))

  (resources "/")
  (not-found "Not Found"))

(def app (wrap-middleware #'routes))

