;; Here is stuff to handle the world
(ns pacgaz.world)




(defn- parse-map
  "Parse a pac map as string into an object I can use in the game"
  
  [str]
  
  (let [map-array   (map vector (map #(.trim %) (.split str "\n")))
        width       (count (ffirst map-array))
        height      (count map-array)
        valid       (every? #(= width (count %)) map-array)
        ]

    {:map-array map-array :width width :height height :valid valid}))

(def pacmap (parse-map pacmap-text))

(defn gtest
  "some test routines"
  []
  (doseq [line (:map-array pacmap)] (prn line)))

;; test it!
(gtest)
