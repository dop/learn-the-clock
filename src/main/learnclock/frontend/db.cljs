(ns learnclock.frontend.db
  (:require [reagent.core :as r]
            [re-frame.core :as rf]))

(defonce tick
  (js/setInterval (fn [] (rf/dispatch [:tick (js/Date.)]))
                  1000))

(rf/reg-sub
 :time
 (fn [db _]
   (let [time (:time db)]
     [(.getHours time)
      (.getMinutes time)
      (.getSeconds time)])))

(rf/reg-event-db
 :initialize
 (fn [_ _]
   {:time (js/Date.)}))

(rf/reg-event-db
 :tick
 (fn [db [_ date]]
   (assoc db :time date)))
