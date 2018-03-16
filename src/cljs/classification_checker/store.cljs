(ns classification_checker.store
  (:require [reagent.core :as reagent]))

(def unchecked-tasks (atom {}))
(def checked-tasks (atom []))
(def current-task (reagent/atom nil))
