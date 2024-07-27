(ns learnclock.frontend.app
  (:require ["react" :as react]
            ["react-dom/client" :as react-client]
            [reagent.core :as r]
            [reagent.dom :as rdom]
            [learnclock.frontend.db :as db]
            [learnclock.frontend.views :refer [svg-clock svg-sun]]
            [learnclock.frontend.time :refer [advance-time]]
            [learnclock.frontend.language :as lang]
            [re-frame.alpha :as rf]))

(defn pad0 [number]
  (.. number toString (padStart 2 "0")))

(defn time-input [{:keys [hours minutes on-change disabled]}]
  (let [type (if disabled "text" "number")]
   [:div {:class "time-input"}
    [:input {:type type :input-mode "numeric" :placeholder "12"
             :value (pad0 hours)
             :on-change #(on-change (-> % .-target .-value js/parseFloat) minutes)
             :disabled disabled}]
    ":"
    [:input {:type type :input-mode "numeric" :placeholder "00"
             :value (pad0 minutes)
             :on-change #(on-change hours (-> % .-target .-value js/parseFloat))
             :disabled disabled}]]))

(defn clock-time-input [{:keys [value on-change]}]
  (let [[hours minutes] value]
    [:div {:class "clock-input"}
     [svg-clock {:hours hours :minutes minutes}]
     [time-input {:hours hours :minutes minutes
                  :disabled false
                  :on-change (fn [h m] (on-change [(mod h 24) (mod m 60)]))}]]))

(defn clock-hands-input [{:keys [value on-change]}]
  (let [[hours minutes] value
        update-time (fn [h m]
                      (println h m)
                      (on-change (advance-time value [h m])))]
    [:div {:class "clock-input"}
     [svg-clock {:hours hours :minutes minutes :on-change update-time}]
     [time-input {:hours hours :minutes minutes :disabled true}]]))

(defn learn-clock-app []
  (let [time1 (r/atom [10 50])
        time2 (r/atom [10 50])]
    (fn []
      (let [time @(rf/subscribe :time)
            [hours minutes seconds] time]
        [:div
         [:div [svg-sun]]
         [:div {:style {:display "flex" :flex-direction "row" :gap "3em" :justify-content "center"}}
          [:div {:class "clock-input"}
           [:div [svg-clock {:hours hours :minutes minutes :seconds seconds}]]
           [time-input {:hours hours :minutes minutes :seconds seconds :disabled true}]
           [:ul (for [variant (lang/time-in-lithuanian [hours minutes])]
                  ^{:key variant} [:li variant])]]
          [:div
           [clock-time-input {:value @time1 :on-change #(reset! time1 %)}]
           [:ul (for [variant (lang/time-in-lithuanian @time1)]
                  ^{:key variant} [:li variant])]]
          [:div
           [clock-hands-input {:value @time2 :on-change #(reset! time2 %)}]
           [:ul (for [variant (lang/time-in-lithuanian @time2)]
                  ^{:key variant} [:li variant])]]]]))))

(defonce app-element
  (do
    (rf/dispatch-sync [:initialize])
    (react-client/createRoot (js/document.getElementById "app"))))

(defn ^:export run
  []
  (.render app-element (r/as-element [learn-clock-app])))

(defn ^:dev/after-load re-render
  []
  (rf/clear-subscription-cache!)
  (run))
