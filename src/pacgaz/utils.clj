(ns pacgaz.utils)

;; Handy trig functions
(defn sin [n] (Math/sin n))
(defn cos [n] (Math/cos n))

(defn map-value
  "Convert value in range [lo .. hi] to [new-lo.. new-hi]"
  [new-lo new-hi val lo hi]

  (let [range (- hi lo)
        new-range (- new-hi new-lo)
        perc (/ (- val lo) range)]
    (+ new-lo (* perc new-range))
    )
  )

(def map-value-zero-to-one (partial map-value 0 1 ))

(defn zero-to-one-cos [time] (map-value-zero-to-one  (cos time) -1 1))
