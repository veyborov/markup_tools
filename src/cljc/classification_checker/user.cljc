(ns classification_checker.user)

(defrecord UserInfo [email])

(defn user-info
  "creates user info"
  [{:keys [email]}]
  (->UserInfo email))
