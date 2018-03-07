(ns common.components
    (:require [reagent.core :as reagent :refer [atom]]
              [antizer.reagent :as ant]))

(defn buttons [on-ok on-cancel on-skip]
  [ant/row
    [ant/col {:span 2 :offset 9} [ant/button {:class "skip-example" :size "large" :icon "question" :on-click on-skip}]]
    [ant/col {:span 2 :offset 1} [ant/button {:class "wrong-example" :size "large" :type "danger" :icon "close" :on-click on-cancel}]]
    [ant/col {:span 2} [ant/button {:class "right-example" :size "large" :type "primary" :icon "check" :on-click on-ok}]] ])
