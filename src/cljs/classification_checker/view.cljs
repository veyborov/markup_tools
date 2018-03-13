(ns classification_checker.view
    (:require 
      [common.components :as common]
      [reagent.core :as reagent :refer [atom]]
      [antizer.reagent :as ant]
      [keybind.core :as key]
      [classification_checker.dispatcher :as action]))

(defn component [title examples]
  (let [example (first examples)]
    (defn click-right [] (action/emit :marked-right example))
    (defn click-wrong [] (action/emit :marked-wrong example))
    (defn click-skip [] (action/emit :skipped example))
    (key/bind! "r" ::next click-right)
    (key/bind! "w" ::next click-wrong)
    (key/bind! "space" ::next click-skip)
    [ant/locale-provider {:locale (ant/locales "ru_RU")}
      [ant/layout
        [ant/layout-header [:h1 title] ]
        [ant/layout-content {:class "content"}
         (if (= nil example) [:div]
           [:div {:style {:width "100%"}}
            [:div {:class "example"} (:value example)]
            [:div {:class "example-class"} (:class example)] ])]
        [ant/layout-footer {:class "footer"}
         (reagent/as-element [common/buttons click-right click-wrong click-skip])]]]))