;; Some code drawing things :)
(ns pacgaz.draw
  (:require [penumbra.opengl :as gl]))

;; Draw a quad

(defn quad [width height]
  (gl/push-matrix
   (gl/translate -0.5 -0.5 0.5)
   (gl/normal 0 0 -1)
   (gl/vertex width height 0)
   (gl/vertex 0 height 0)
   (gl/vertex 0 0 0)
   (gl/vertex width 0 0)))

;; Set a colour from an array
(defn color [col]
  (let [[r g b a] col]
    (gl/color r g b a)))

;; Draw a cube

(defn cube [thickness height]
  (gl/push-matrix
   (gl/draw-quads
    (dotimes [_ 4]
      (gl/rotate 90 0 1 0)
      (quad thickness height))
    (gl/rotate 90 1 0 0)
    (quad thickness thickness)
    (gl/translate 0 0 (- height))
    (quad thickness thickness))))

;; Draw a colourful cube
(defn cube-col [thickness height col]
  (gl/color col)
  (cube thickness height)
  )

(defn draw-one-tri []
  (gl/draw-triangles
   (gl/color 1 0 0) (gl/vertex 1 0)
   (gl/color 0 1 0) (gl/vertex -1 0)
   (gl/color 0 0 1) (gl/vertex 0 1.86))
  )


