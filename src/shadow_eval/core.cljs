(ns shadow-eval.core
  (:require

    ;; read
    [cljs.tools.reader.reader-types :as rt]
    [cljs.tools.reader :as r]


    ;; evaluate
    [cljs.js :as cljs]
    [shadow.bootstrap :as boot]
    [cljs.env :as env]

    ;; view
    [re-view.core :as v :refer [defview]]
    [re-db.d :as d]))

(defview layout []
         [:div
          "Hello"])

(defn render []
  (v/render-to-dom (layout) "shadow-eval"))

(defn print-result [{:keys [error value] :as result}]
  (js/console.log "result" result)
  (js/console.log "compile-state" @boot/compile-state-ref)
  (set! (.-innerHTML (js/document.getElementById "dump")) value))

(defn compile-it []
  (cljs/eval-str
    boot/compile-state-ref
    "(ns my.user (:require [reagent.core :as r]))
     (doall (for [n (range 10)] n))
     (map inc [1 2 3])"
    "[test]"
    {:eval
           (fn [{:keys [source cache lang name]}]
             (js/console.log "Eval" name lang {:cache  (some-> cache (str) (subs 0 20))
                                               :source (some-> source (subs 0 150))})
             (js/eval source))
     :load boot/load}
    print-result))

(defn start []
  (boot/init compile-it))
