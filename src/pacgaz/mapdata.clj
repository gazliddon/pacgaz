;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns pacgaz.mapdata
  (:use [clojure.string :only (join)])
  (:require [dk.ative.docjure.spreadsheet :as dj] ))
 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Different map element types

(def map-bit-types
  {
    \# { :name "wall"   :collide true  }
    \- { :name "space"  :collide false }
    \. { :name "pill"   :collide false }
    \P { :name "pacman" :collide false }
    \! { :name "error"  :collide false }   ;; Unknown piece
    })

;; Valid bit for unrecognised chars in the map data
(defn- get-map-bit-type [c] (get map-bit-types c (get map-bit-types \!)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Excel file magubbins

(defn- get-cell-data
  "Dump an excel cell into x,y and text"
  [cell]
  (let [x    (.getRowIndex cell)
        y    (.getColumnIndex cell)
        text (.getStringCellValue cell)]

    {:x x :y y :text text})
  )

(defn- iterate-cells
  "Iterate through all of the cells in the first sheet of this workbook"
  [file func]
  (defn dump-row [row]
    (map func (dj/cell-seq row)))
  
  (let [rows (-> file
                 (dj/load-workbook)
                 (first)
                 (dj/row-seq))]
    (flatten (map dump-row rows))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Transform into game data functions

(defn- get-map-bit-cell [cell]
  (let [ret-cell (get-cell-data cell)
        chr (first (:text ret-cell))
        map-bit (get-map-bit-type chr)]
    (merge ret-cell map-bit)))
 
(defn- xls-to-tiles
  "Turn an xls file into a bunch of map bit tiles {:x :y :map-bit}"
  [file]
 (iterate-cells file get-map-bit-cell))
 
(defn- tiles-to-hash
  "Turn a sequence of tiles into a hash keyed by [x y]"
  [tiles]
  (reduce #(assoc %1 [ (:x %2) (:y %2) ] %2) {} tiles))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Public API

;; I really like the reduce to make a hash map keyed by tile [x y] pos
(defn load-level
  "Turn an xls file into a level"
  [file]
  (let [ tiles       (iterate-cells file get-map-bit-cell)
         pos-to-bit  (reduce #(assoc %1 [ (:x %2) (:y %2) ] %2) {} tiles)
        ]
    { :tiles      tiles
      :pos-to-bit pos-to-bit }))

(defn get-tiles-of-type
  "Get all of the tiles of this named type from the map"
  [level type]
  (filter #(= type (:name %)) (:tiles level)))

(defn get-tile
  "Get the tile at x,y from this map"
  [level x y]
  (assert false)
  )

;;; ends

;;; Todo?
;;; Turn add hoc maps into Records?
;;; Make level into a protocol
