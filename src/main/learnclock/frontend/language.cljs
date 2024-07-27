(ns learnclock.frontend.language
  (:require [clojure.string :as str]))

(defn describe-hours [hours]
  (case hours
    0 ["vidurnaktis"]
    1 ["pirma valanda"]
    2 ["dvi valandos"]
    3 ["trys valandos"]
    4 ["keturios valandos"]
    5 ["penkios valandos"]
    6 ["šešios valandos"]
    7 ["septynios valandos"]
    8 ["aštuonios valandos"]
    9 ["devynios valandos"]
    10 ["dešimt valandų"]
    11 ["vienuolika valandų"]
    12 ["dvylika valandų"]
    13 ["trylika valandų", "pirma valanda"]
    14 ["keturiolika valandų", "dvi valandos"]
    15 ["penkiolika valandų", "trys valandos"]
    16 ["šešiolika valandų", "keturios valandos"]
    17 ["septyniolika valandų", "penkios valandos"]
    18 ["aštuoniolika valandų", "šešios valandos"]
    19 ["devyniolika valandų", "septynios valandos"]
    20 ["dvidešimt valandų", "aštuonios valandos"]
    21 ["dvidešimt pirma valanda", "devynios valandos"]
    22 ["dvidešimt dvi valandos", "dešimt valandų"]
    23 ["dvidešimt trys valandos", "dvienuolika valandų"]))

(defn hour-in-lithuanian [hour]
  (case hour
    0 ["vidurnaktis"]
    1 ["pirma valanda"]
    2 ["dvi valandos"]
    3 ["trys valandos"]
    4 ["keturios valandos"]
    5 ["penkios valandos"]
    6 ["šešios valandos"]
    7 ["septynios valandos"]
    8 ["aštuonios valandos"]
    9 ["devynios valandos"]
    10 ["dešimt valandų"]
    11 ["vienuolika valandų"]
    12 ["dvylika valandų"]
    13 ["trylika valandų", "pirma valanda"]
    14 ["keturiolika valandų", "dvi valandos"]
    15 ["penkiolika valandų", "trys valandos"]
    16 ["šešiolika valandų", "keturios valandos"]
    17 ["septyniolika valandų", "penkios valandos"]
    18 ["aštuoniolika valandų", "šešios valandos"]
    19 ["devyniolika valandų", "septynios valandos"]
    20 ["dvidešimt valandų", "aštuonios valandos"]
    21 ["dvidešimt pirma valanda", "devynios valandos"]
    22 ["dvidešimt dvi valandos", "dešimt valandų"]
    23 ["dvidešimt trys valandos", "dvienuolika valandų"]))

(defn cross [ss1 ss2]
  (into [] (for [s1 ss1 s2 ss2] (str s1 " " s2))))

(defn half-next-hour-in-lithuanian [hour]
  (cross ["pusė"]
         (case (mod hour 12)
           0 ["pirmos" "pirmos valandos"]
           1 ["dviejų" "antros valandos"]
           2 ["trijų" "trečios valandos"]
           3 ["keturių" "ketvirtos valandos"]
           4 ["penkių" "penktos valandos"]
           5 ["šešių" "šeštos valandos"]
           6 ["septynių" "septintos valandos"]
           7 ["aštuonių" "aštuntos valandos"]
           8 ["devynių" "devintos valandos"]
           9 ["dešimt" "dešimtos valandos"]
           10 ["vienuolikos" "vienuoliktos valandos"]
           11 ["dvylikos" "dvyliktos valandos"])))

(def minute-numbers
  {1 "viena minutė"
   2 "dvi minutės"
   3 "trys minutės"
   4 "keturios minutės"
   5 "penkios minutės"
   6 "šešios minutės"
   7 "septynios minutės"
   8 "aštuonios minutės"
   9 "devynios minutės"
   10 "dešimt minučių"
   11 "vienuolika minučių"
   12 "dvylika minučių"
   13 "trylika minučių"
   14 "keturiolika minučių"
   15 "penkiolika minučių"
   16 "šešiolika minučių"
   17 "septyniolika minučių"
   18 "aštuoniolika minučių"
   19 "devyniolika minučių"})

(def minute-tens
  {1 "dešimt"
   2 "dvidešimt"
   3 "trisdešimt"
   4 "keturiasdešimt"
   5 "penkiasdešimt"})

(defn generic-time-in-lithuanian [[h m]]
  (let [hours (hour-in-lithuanian h)]
    (if (= 0 m)
      hours
      (cross hours
             [(str (minute-tens (Math/floor (/ m 10)))
                   " "
                   (or (minute-numbers (mod m 10)) "minučių"))]))))

(defn time-in-lithuanian [time]
  (let [[h m] time
        next-hour (fn [] (hour-in-lithuanian (mod (inc h) 24)))]
    (into (cond
            (= m 55) (cross ["be penkių minučių"] (next-hour))
            (= m 50) (cross ["be dešimties minučių"] (next-hour))
            (= m 45) (cross ["be penkiolikos minučių"] (next-hour))
            (= m 40) (cross ["be dvidešimties minučių"] (next-hour))
            (= m 30) (half-next-hour-in-lithuanian h)
            :else [])
          (generic-time-in-lithuanian time))))

(defn describe-minutes [minutes]
  (cond
    (< minutes 20)
    (minute-numbers minutes)
    :else
    (str (minute-tens (Math/floor (/ minutes 10)))
         " "
         (or (minute-numbers (mod minutes 10)) "minučių"))))

(defn describe-time [h m]
  (str (first (describe-hours h)) " " (describe-minutes m)))

;; nulis valandų
;; pirma valanda
;; dvi valandos
;; trys valandos
;; keturios valandos
;; penkios valandos
;; šešios valandos
;; septynios valandos
;; aštuonios valandos
;; devynios valandos
;; dešimt valandų

;; penkiolika valandų trisdešimt keturios minutės

;; pirma valanda dešimt minučių
;; antra valanda dešimt minučių
;; trys valandos dešimt minučių

;; penkiolika minučių po pirmos
;; dvidešimt minučių po antros
;; penkios minutės iki trijų
;; penkios minutės iki trijų

;; pusė keturių, penkiolika trisdešimt
;; pusė penkių, šešiolika trisdešimt
