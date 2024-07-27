(ns learnclock.frontend.time)

(defn advance-minute [new-hour new-minute old-minute]
  (let [next-hour
        (mod (+ new-hour
                (cond
                  ;; crossed 0 forwards
                  (> (- old-minute new-minute) 30) 1
                  ;; crossed 0 backwards
                  (< (- old-minute new-minute) -30) -1
                  :else 0))
             24)]
    [next-hour new-minute]))

(defn advance-hour [old-hour new-hour]
  (let [period (if (< old-hour 12) :am :pm)
        hour (mod old-hour 12)]
    (cond
      (= (mod old-hour 12) (mod new-hour 12))
      old-hour

      ;; crossing 0 forwards
      (> (- hour new-hour) 6)
      (case period
        :pm new-hour
        :am (+ 12 new-hour))

      ;; crossing 0 backwards
      (< (- hour new-hour) -6)
      (case period
        :pm new-hour
        :am (+ 12 new-hour))

      :else
      (case period
        :pm (+ 12 new-hour)
        :am new-hour))))

(defn advance-time
  "Figure out current time in 24h format if previous time was OLD-STATE
  in 24h format and currently selected time is NEW-STATE in 12h
  format.

  This is used to calculate currently selected time from a stream of
  inputs when visually moving clock handles."
  [old-state new-state]
  (let [[old-hour old-minute old-period] old-state
        [new-hour new-minute] new-state]
    (cond
      (not (= new-minute old-minute))
      (advance-minute new-hour new-minute old-minute)

      (not (= new-hour old-hour))
      [(advance-hour old-hour new-hour) new-minute]

      :else
      new-state)))
