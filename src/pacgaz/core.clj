(ns pacgaz.core
  (:require  [penumbra.opengl :as gl])
  (:use [pacgaz.draw])
  (:require [penumbra.app :as app]))

(defn init [state]
  (app/vsync! true)
  (gl/enable :depth-test)
  (gl/enable :normalize)
  (gl/enable :depth-test)
  (gl/disable :cull-face)
  
  state)

(defn reshape [[x y width height] state]
  (gl/frustum-view 60.0 (/ (double width) height) 1.0 100.0)
  (gl/load-identity)
  state)

(defn sin [n] (Math/sin n))
(defn cos [n] (Math/cos n))

(defn draw-one-tri []
  (gl/draw-triangles
   (gl/color 1 0 0) (gl/vertex 1 0)
   (gl/color 0 1 0) (gl/vertex -1 0)
   (gl/color 0 0 1) (gl/vertex 0 1.86))
  )

(defn recur-tri [func]
  (gl/push-matrix
   (gl/scale 0.75 0.75 0.75)
   (gl/rotate 25.5 0 1 0)
   
   (gl/push-matrix
    (gl/translate 0 0 1)
    (func)
    (gl/translate 0 0 -2)
    (func)
    )
   
   #(recur-tri func)
   )
  )

(defn draw-world []
  (def draw #(cube 1.5 0.5))
  (nth (iterate recur-tri draw) 3))

;;; Scene init, resize handling, updates, input
(defn mouse-scene-rotation [state]
  (gl/rotate (:rot-x state) 1 0 0)
  (gl/rotate (:rot-y state) 0 1 0))

;; 

;; Normalise a value to 0 .. 1
(defn norm[val lo hi]
  (def range (- hi lo))
  (def pos (- val lo))
  (/ pos range)
  )

(defn generate-cube []
  )

(defn osc[time] (norm (cos time) -1 1))

(defn display-loop [time state]

  (def r (osc (* 1 time)))
  (def g (osc (* 3.3 time)))
  (def b (osc (* 5.5 time)))
  (def a (osc (* 3 time)))
  
  (gl/color r g b a)
  (gl/translate 0 -0.93 -3)
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


;; 

;; A cube object in the game

(defstruct pos :x :y :z)
(defstruct vel :x :y :z)
(defstruct col :r :g :b :a)
(defstruct game-cube :col :pos :vel :number :create-time)

(defn make-game-cube [rnd]
  (struct-map game-cube
    :pos {}
    :vel 2
           :number rnd)
  )


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


