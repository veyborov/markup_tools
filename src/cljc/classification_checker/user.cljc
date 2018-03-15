(ns classification_checker.user)

(defrecord UserInfo [email])

(defn user
  "creates user info"
  [{:keys [email]}]
  (->User email))
