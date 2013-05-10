(ns pacgaz.mapdata

  (:require clojure.pprint)
  (:use [clojure.string :only (join)]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Different map element types

(def map-bit-types
  {
    \* { :name "wall"   :collide true  }
    \. { :name "space"  :collide false }
    \o { :name "pill"   :collide false }
    \P { :name "pacman" :collide false }
    })

;; Valid bit for unrecognised chars in the map data
(def err-bit { :name "error" :collide false})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Turn a string into an array of map bits
;; Parse the map from a string, like the one at the bottom of this file
;; and turn it into a sequence

(defn- parse-map
  
  "Parse a pac map as string into an object I can use in the game
   checks to see if all of the lines are the same width"
  
  [str]

  ;; Turns a chr into a has with info about the map bit this represents
  ;; along with its x y pos on the map
  
  (defn- char-to-map-data [w idx chr]
    (merge { :x (rem idx w) :y (quot idx w) } (get map-bit-types chr err-bit)))
  
  (let [lines    (map #(.trim %) (.split str "\n"))
        width    (count (first lines))
        joined   (join lines)
        map-bits (map-indexed (partial char-to-map-data width) joined)
       ]
  
    {:valid (every? #(= width (count %)) lines)
     :width width :height (count lines)
     :map map-bits
     :lines lines
     :joined joined}))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Some test data and code

;; Ha! Map doesn't care about spurious spaces in data :)

(def test-map
  "*********
   *.o...o.*
   *.......*
   *...P...*
   *.......*
   *.o...o.*
   *********")

(defn get-bits-of-type [type]
  (filter #(= type (:name %)) (:map (parse-map test-map))))

(get-bits-of-type "wall")
