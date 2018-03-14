(ns classification_checker.types)

(defprotocol )
(defrecord ParaphraseExample [utterance1 utterance2 is-same? assessor mark-time])

;(defn example-from-class-value [class value] ({:class class :value value :is-right? nil :assessor nil}))
;(defn example-right [assessor example] (conj example {:is-right? true :assessor assessor}))
;(defn example-wrong [assessor example] (conj example {:is-right? false :assessor assessor}))
;(defn example-unknown [assessor example] (conj example {:is-right? nil}))
;(defn example-equals [ex1 ex2] (.equals ex1 ex2))
