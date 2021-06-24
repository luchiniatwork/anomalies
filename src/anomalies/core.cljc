(ns anomalies.core
  (:refer-clojure :exclude [throw])
  (:require [clojure.spec.alpha :as s]))

(def ^:private info
  {::unavailable        {::retry-possible? true
                         ::fix :make-sure-callee-healthy}
   ::interrupted        {::retry-possible? true
                         ::fix :stop-interrupting}
   ::incorrect          {::retry-possible? false
                         ::fix :fix-caller-bug}
   ::forbidden          {::retry-possible? false
                         ::fix :fix-caller-creds}
   ::unsupported        {::retry-possible? false
                         ::fix :fix-caller-verb}
   ::not-found          {::retry-possible? false
                         ::fix :fix-caller-noun}
   ::conflict           {::retry-possible? false
                         ::fix :coordinate-with-callee}
   ::fault              {::retry-possible? false
                         ::fix :fix-callee-bug}
   ::busy               {::retry-possible? true
                         ::fix :backoff-and-retry}})

(def ^:private default-anom ::unavailable)

(s/def ::category #{::unavailable
                    ::interrupted
                    ::incorrect
                    ::forbidden
                    ::unsupported
                    ::not-found
                    ::conflict
                    ::fault
                    ::busy})

(s/def ::message string?)

(s/def ::id qualified-keyword?)

(s/def ::anomaly (s/keys :req [::category]
                         :opt [::message ::id]))

(defn ^:private merge-info [m]
  (let [category (or (::category m) default-anom)]
    (-> m
        (assoc ::category category)
        (merge (get info category)))))

(defn ->Anom
  ([]
   (->Anom nil nil))
  ([message]
   (->Anom message nil))
  ([message m]
   (merge-info (merge (if message {::message message} {}) m))))

(defn throw-anom
  ([]
   (throw-anom nil nil))
  ([message]
   (throw-anom message nil))
  ([message m]
   (throw (ex-info message (->Anom message m)))))
