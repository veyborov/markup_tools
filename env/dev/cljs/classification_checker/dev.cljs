(ns ^:figwheel-no-load classification-checker.dev
  (:require
    [classification-checker.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
