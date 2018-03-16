(ns classification_checker.example)

(defprotocol ParaphraseClassification
  (id [this])
  (right [this assessor timestamp])
  (wrong [this assessor timestamp])
  (unknown [this assessor timestamp]))

(defrecord ParaphraseExample [utterance1 utterance2 is-same? assessor mark-time]
  ParaphraseClassification
  (id [this] (if (some? this) (hash this)))
  (right [this assessor timestamp] (->ParaphraseExample utterance1 utterance2 true assessor timestamp))
  (wrong [this assessor timestamp] (->ParaphraseExample utterance1 utterance2 false assessor timestamp))
  (unknown [this assessor timestamp] (->ParaphraseExample utterance1 utterance2 nil assessor timestamp)))

(defn paraphrase-example
  "creates example"
  [{:keys [utterance1 utterance2]}]
  (->ParaphraseExample utterance1 utterance2 nil nil nil))

