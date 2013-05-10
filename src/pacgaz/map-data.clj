(ns pacgaz.map-data
  (:require [pacgaz.utils :as u]))

;; Textual version of the map

(def pacmap-test
  "*********
   *.o...o.*
   *.......*
   *...P...*
   *.......*
   *.o...o.*
   *********")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Different map element types

;; All of the map types
(def map-bit-types
  {
    \* { :name "wall"   :collide true  }
    \. { :name "space"  :collide false }
    \o { :name "pill"   :collide false }
    \P { :name "pacman" :collide false }
    })

;; Valid bit for unrecognised chars in the map data
(def error-bit
  { :name "error" :collide false})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Turn a string into an array of map bits
(defn- string-to-map-data [str]
  (vec (map #(get map-bit-types % error-bit) str)))

;; Parse the map from a string, like the one at the top of this file
(defn- parse-map

  "Parse a pac map as string into an object I can use in the game
   checks to see if all of the lines are the same width"
  
  [str]
  
  (let [lines    (map #(.trim %) (.split str "\n"))
        map-bits (vec (map string-to-map-data lines))
        width    (count (first map-bits))
        height   (count map-bits)
        valid    (every? #(= width (count %)) map-bits) 
       ]

    {:valid valid
     :width width :height height
     :map-bits map-bits
     :lines lines }))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Map processing stuff

(defn iterate-pacmap
  "Iterate through a pac man map"
  [pac-map func]
  (u/iterate-2d-array (:map-bits pac-map) (:width pac-map) (:height pac-map) func))

(def iterate-test-map (partial iterate-pacmap (parse-map pacmap-test)))

(defn draw-func [bit x y]
  bit)

(pprint (iterate-test-map draw-func))


