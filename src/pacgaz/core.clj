(ns pacgaz.core

  (:use [penumbra.opengl])
  (:require [penumbra.app :as app]))

(defn init [state]
  (app/vsync! true)
  (enable :depth-test)
  
  state)

(defn reshape [[x y width height] state]
  (frustum-view 60.0 (/ (double width) height) 1.0 100.0)
  (load-identity)
  state)

(defn sin [n] (Math/sin n))
(defn cos [n] (Math/cos n))

(defn draw-one-tri []
  (draw-triangles
   (color 1 0 0) (vertex 1 0)
   (color 0 1 0) (vertex -1 0)
   (color 0 0 1) (vertex 0 1.86))
  )

(defn recur-tri [func]
  (push-matrix
   (scale 0.75 0.75 0.75)
   (rotate 25.5 0 1 0)
   
   (push-matrix
    (translate 0 0 1)
    (func)
    (translate 0 0 -2)
    (func)
    )
   
   #(recur-tri func)
   )
  )


;; Draw a quad
(defn quad [width height]
  (push-matrix
   (translate -0.5 -0.5 0.5)
   (normal 0 0 -1)
   (vertex width height 0)
   (vertex 0 height 0)
   (vertex 0 0 0)
   (vertex width 0 0)))

;; Draw a cube
(defn cube [thickness height]
  (draw-quads
   (dotimes [_ 4]
     (rotate 90 0 1 0)
     (quad thickness height))
   (rotate 90 1 0 0)
   (quad thickness thickness)
   (translate 0 0 (- height))
   (quad thickness thickness)))

(defn draw-world []
  (def draw #(cube 0.5 0.5))
  (nth (iterate recur-tri draw) 3))

;;; Scene init, resize handling, updates, input
(defn mouse-scene-rotation [state]
  (rotate (:rot-x state) 1 0 0)
  (rotate (:rot-y state) 0 1 0))


;; Normalise a value to 0 .. 1
(defn norm[val lo hi]
  (def range (- hi lo))
  (def pos (- val lo))
  (/ pos range)
  )

(defn osc[time] (norm (cos time) -1 1))

(defn display-loop [time state]
  (color 1 (osc (* 10 time)) 1)
  (translate 0 -0.93 -3)
  ;; (rotate (rem (* 20 time) 360) 0 1 0)
  (mouse-scene-rotation state)
  (draw-world )
  )

(defn mouse-drag [[dx dy] [x y] button state]
  (assoc state
    :rot-x (+ (:rot-x state) dy)
    :rot-y (+ (:rot-y state) dx)))



;; Game display loop

(defn display [[delta time] state]
  (display-loop time state)
  (app/repaint!))

;; Start the display loop
(defn start-display-loop []

  (let [ state { :rot-x 0 :rot-y 0 }
        app-options {
                     :display display
                     :reshape reshape
                     :init init
                     :mouse-drag mouse-drag }]

    (app/start app-options state)))

(.start (Thread. start-display-loop))


