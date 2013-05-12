(ns pacgaz.mapdata
  (:require clojure.pprint)
  (:require [dk.ative.docjure.spreadsheet :as dj])
  (:use [clojure.string :only (join)]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Different map element types

(def map-bit-types
  {
    \# { :name "wall"   :collide true  }
    \- { :name "space"  :collide false }
    \. { :name "pill"   :collide false }
    \P { :name "pacman" :collide false }
    })

;; Valid bit for unrecognised chars in the map data
(def err-bit { :name "error" :collide false})
(defn- get-map-bit-type [c] (get map-bit-types c err-bit))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Turn a string into an array of map bits
;; Parse the map from a string, like the one at the bottom of this file
;; and turn it into a sequence
(defn- char-to-map-data [w idx chr]
  (merge { :x (rem idx w) :y (quot idx w) } (get-map-bit-type chr)))
 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Public api here


(defn load-level
  "Loads a level from an XLSX file
   assumes that the map is in the first sheet"
  [file]

  (defn row-to-cells [w row]
    (let [ cell-data (vec (map dj/read-cell (dj/cell-seq row))) ]
      (map  #(get cell-data % " ") (range w))))
  
  (let [wb   (dj/load-workbook file )
        rows (dj/row-seq  (first (dj/sheet-seq wb)))
        w    (apply max (map  #(count (dj/cell-seq %)) rows))
        h    (count rows)
        joined (join (flatten (map (partial row-to-cells w) rows)))
        map-bits (map-indexed (partial char-to-map-data w) joined)
        ]
    {:height h  :width w
     :joined joined
     :map map-bits
     })
  )

(defn get-tiles-of-type
  "get all of the tiles of this named type from the map"
  [level type]
  (filter #(= type (:name %)) (level :map)))

(defn get-tile
  "get the tile at x,y from this map"
  [level x y]
  (assert false)
  )

;;; ends
