(ns classification_checker.common_components
    (:require [reagent.core :as reagent :refer [atom]]
              [antizer.reagent :as ant]))

(defn buttons []
  [ant/row {:gutter 24}
    [ant/col {:span 2 :offset 9} [ant/button {:class "skip-example" :size "large"} [ant/icon {:type "question"}] ]]
    [ant/col {:span 2 :offset 1} [ant/button {:class "wrong-example" :size "large" :type "danger"} [ant/icon {:type "close"}] ]]
    [ant/col {:span 2} [ant/button {:class "right-example" :size "large" :type "primary"} [ant/icon {:type "check"}] ]] ])
