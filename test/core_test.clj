(ns core-test
  (:require [anomalies.core :as anom]
            [clojure.spec.alpha :as s]
            [clojure.test :refer :all]))

(def ^:private info
  {::anom/unavailable        {::anom/retry-possible? true
                              ::anom/fix :make-sure-callee-healthy}
   ::anom/interrupted        {::anom/retry-possible? true
                              ::anom/fix :stop-interrupting}
   ::anom/incorrect          {::anom/retry-possible? false
                              ::anom/fix :fix-caller-bug}
   ::anom/forbidden          {::anom/retry-possible? false
                              ::anom/fix :fix-caller-creds}
   ::anom/unsupported        {::anom/retry-possible? false
                              ::anom/fix :fix-caller-verb}
   ::anom/not-found          {::anom/retry-possible? false
                              ::anom/fix :fix-caller-noun}
   ::anom/conflict           {::anom/retry-possible? false
                              ::anom/fix :coordinate-with-callee}
   ::anom/fault              {::anom/retry-possible? false
                              ::anom/fix :fix-callee-bug}
   ::anom/busy               {::anom/retry-possible? true
                              ::anom/fix :backoff-and-retry}})

(defn is-valid-anom? [a {:keys [message category id]}]
  (is (s/valid? ::anom/anomaly a))
  (when message
    (is (= message (::anom/message a))))
  (when id
    (is (= id (::anom/id a))))
  (is (= category (::anom/category a)))
  (is (= (get-in info [category ::anom/retry-possible?])
         (get a ::anom/retry-possible?)))
  (is (= (get-in info [category ::anom/fix])
         (get a ::anom/fix))))

(deftest should-create-valid-anom
  (testing "no param"
    (is-valid-anom? (anom/->Anom)
                    {:category ::anom/unavailable}))
  
  (testing "single message param"
    (is-valid-anom? (anom/->Anom "foobar")
                    {:category ::anom/unavailable
                     :message "foobar"}))

  (testing "providing category"
    (is-valid-anom? (anom/->Anom "barfoo" {::anom/category ::anom/not-found})
                    {:category ::anom/not-found
                     :message "barfoo"}))

  (testing "providing id"
    (is-valid-anom? (anom/->Anom "foobar" {::anom/category ::anom/busy
                                           ::anom/id :foo/bar})
                    {:category ::anom/busy
                     :message "foobar"
                     :id :foo/bar}))

  (testing "providing message, category, id"
    (is-valid-anom? (anom/->Anom "barfoo" {::anom/category ::anom/forbidden
                                           ::anom/id :bar/foo})
                    {:category ::anom/forbidden
                     :message "barfoo"
                     :id :bar/foo})))

(deftest should-throw
  (is (thrown? clojure.lang.ExceptionInfo
               (anom/throw-anom))))
