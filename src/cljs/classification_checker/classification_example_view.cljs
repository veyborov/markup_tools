(ns classification_checker.classification_example_view
    (:require 
      [classification_checker.common_components :as common]
      [reagent.core :as reagent :refer [atom]]
      [antizer.reagent :as ant]
      [keybind.core :as key]))

(defn component [example example-class]
  (key/bind! "j" ::next #(println "!!!!!!!"))

  [ant/locale-provider {:locale (ant/locales "ru_RU")}
    [ant/layout
      [ant/layout-header [:h1 "Примеры значат одно и то же?"] ]
      [ant/layout-content {:class "content"}
        [:div {:style {:width "100%"}}
          [:div {:class "example"} example]
          [:div {:class "example-class"} example-class] ]]
      [ant/layout-footer {:class "footer"}
       (reagent/as-element [common/buttons]) ] ] ])
