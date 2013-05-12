;; Some code drawing things :)
(ns pacgaz.draw
  (:require [penumbra.opengl :as gl ])
  (:require [pacgaz.utils    :as u  ])
  )


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
   (gl/scale thickness height 1)
   (gl/draw-quads
    (dotimes [_ 4]
      (gl/rotate 90 0 1 0)
      (quad 1 1))
    (gl/rotate 90 1 0 0)
    (quad 1 1)
    (gl/rotate 180 1 0 0)
    (gl/translate 0 0 (- 1))
    (quad 1 1))))

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


(defn range-steps
  "Make a range from lo .. hi in n steps"
  [lo hi n]
  (range lo hi (/ (- hi lo) (float n))))
  
(defn zero-to-one-seq [points] (range-steps 0 1 points))
(defn zero-to-twopi-seq [points] (range-steps 0 u/twopi points))

(defn circle-point [t] (list (u/cos t) (u/sin t)))
(defn circle-points [num] (map circle-point (zero-to-twopi-seq num)))
(defn circle-points-rad [num rad]
  (map (fn[p](vec (map (partial * rad) p))) (circle-points num))
)

(defn circle
  
  "Draw a line circle at x y z @ rad with segs segments"

  [segs rad ]

  (def the-verts
    (map (fn[v] [  (v 0) (v 1) 0] )
         (circle-points-rad segs rad)))

  (gl/draw-line-loop  (doseq [v the-verts] (apply gl/vertex v))))

(defn sphere [rows segs rad]
  (doseq [r (range-steps 0 u/pi rows)]
    (gl/push-matrix
     (gl/translate 0 0 (* 1 (* (u/cos r) rad)) )
     (circle segs (* rad (u/sin r)))
     )
    )
  )
