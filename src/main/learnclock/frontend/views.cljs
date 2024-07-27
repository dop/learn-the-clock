(ns learnclock.frontend.views
  (:require [learnclock.frontend.db :as db]
            [reagent.core :as r]))

(def minute-radians (/ Math/PI 30))
(def hour-radians (/ Math/PI 6))

(defn clock-face []
  [:<>
   [:circle {:cx 50 :cy 50 :r 48 :fill "white" :stroke "black" :stroke-width 2}]
   ;; dots
   (for [n (range 60)]
     ^{:key n}
     [:circle {:cx (+ 50 (* 45 (Math/sin (* n minute-radians))))
               :cy (- 50 (* 45 (Math/cos (* n minute-radians))))
               :r (if (= 0 (mod n 5)) 1 0.5)}])
   ;; numbers
   (for [n (range 12)]
     ^{:key n}
     [:<>
      [:text.clock-number
       {:x (+ 50 (* 38 (Math/sin (* n hour-radians))))
        ;; Not sure why 51 looks better.
        :y (- 51 (* 38 (Math/cos (* n hour-radians))))}
       (str (if (zero? n) 12 n))]])])

(defn clock-hand
  [{:keys [id length angle width interactive]
    :or {width 1.0}}]
  [:g
   (when interactive
     [:circle {:data-id id
               :cx (+ 50 (* length 0.7 (Math/sin angle)))
               :cy (- 50 (* length 0.7 (Math/cos angle)))
               :r 4}])
   [:line {:data-id id
           :x1 50
           :y1 50
           :x2 (+ 50 (* length (Math/sin angle)))
           :y2 (- 50 (* length (Math/cos angle)))
           :stroke "black"
           :stroke-width width
           :stroke-linecap "round"}]])

(defn svg [width height & children]
  (apply conj [:svg {:viewBox "0 0 100 100" :xmlns "http://www.w3.org/2000/svg"
                     :width width :height height}]
         children))

(defn find-ancestor-by-tag [el tag]
  (when-let [parent (.-parentNode el)]
    (if (= tag (.-tagName parent))
      parent
      (find-ancestor-by-tag parent tag))))

(defn clock-mouse-position->time [event steps]
  (let [svg (find-ancestor-by-tag (.-target event) "svg")
        rect (.getBoundingClientRect svg)
        x (- (.-pageX event) (+ (.-x rect) (/ (.-width rect) 2)))
        y (- (.-pageY event) (+ (.-y rect) (/ (.-height rect) 2)))
        angle (* (Math/atan2 x (- y)) (/ 180 Math/PI))]
    (mod (Math/round (/ (if (< angle 0) (+ 360 angle) angle)
                    (/ 360 steps)))
         steps)))

(defn clock-mouse-position->minute [event]
  (clock-mouse-position->time event 60))

(defn clock-mouse-position->hour [event]
  (clock-mouse-position->time event 12))

(defn svg-clock []
  (let [hovering? (r/atom false)
        dragging? (r/atom false)]
    (fn [{:keys [width height hours minutes seconds on-change]
          :or {width 300 height 300}}]
      (let [interactive? (js/Boolean on-change)
            on-down
            (fn [event]
              (reset! dragging? @hovering?))

            on-up
            (fn [event]
              (reset! dragging? nil))

            on-move
            (fn [event]
              (case @dragging?
                :minutes (on-change hours (clock-mouse-position->minute event))
                :hours (on-change (clock-mouse-position->hour event) minutes)
                (case (.. event -target -dataset -id)
                  "hours" (reset! hovering? :hours)
                  "minutes" (reset! hovering? :minutes)
                  (reset! hovering? nil))))

            on-leave
            (fn [event]
              (reset! dragging? nil)
              (reset! hovering? nil))

            attrs
            (if interactive?
              {:on-mouse-leave on-leave
               :on-mouse-down on-down
               :on-mouse-up on-up
               :on-mouse-move on-move}
              {})]
        [svg width height
         [:g (conj attrs
                   {:style
                    {:cursor (cond
                               @dragging? "grabbing"
                               @hovering? "grab"
                               :else      "default")}})
          [clock-face]
          [clock-hand {:id "hours"
                       :interactive interactive?
                       :length 30
                       :angle (* (+ hours (/ minutes 60.0)) hour-radians)
                       :width 1.5}]
          [clock-hand {:id "minutes"
                       :interactive interactive?
                       :length 38
                       :angle (* (+ minutes (/ seconds 60.0)) minute-radians)
                       :width 1}]
          (when seconds
            [clock-hand {:length 42 :angle (* seconds minute-radians) :width 0.5}])]]))))

(defn svg-polygon [attrs & points]
  (let [points-string (clojure.string/join " " (map (fn [[x y]] (str x "," y)) points))]
    [:polygon (conj attrs {:points points-string})]))

(defn svg-sun []
  (let [steps 12
        radians (/ (* Math/PI 2) steps)]
    [svg 100 100
     [:circle {:cx 50 :cy 50 :r 30 :stroke "orange" :fill "yellow"}]
     [:g {:id "sun-rays"}
       (for [n (range steps)]
         ^{:key n}
         [svg-polygon {:stroke "orange" :fill "yellow"}
          [(+ 50 (* 45 (Math/sin (* n radians))))
           (- 50 (* 45 (Math/cos (* n radians))))]
          [(+ 50 (* 35 (Math/sin (* (- n 0.4) radians))))
           (- 50 (* 35 (Math/cos (* (- n 0.4) radians))))]
          [(+ 50 (* 35 (Math/sin (* (+ n 0.4) radians))))
           (- 50 (* 35 (Math/cos (* (+ n 0.4) radians))))]])
      [:animateTransform
       {:xlinkHref "#sun-rays"
        :attributeName "transform"
        :attributeType "XML"
        :from "0 50 50"
        :to "360 50 50"
        :dur "20s"
        :repeatCount "indefinite"
        :type "rotate"}]]]))
