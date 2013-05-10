(ns pacgaz.core
  (:require [penumbra.opengl :as gl])
  
  (:require [pacgaz.draw     :as d])
  (:require [pacgaz.utils    :as u])
  (:require [pacgaz.mapdata  :as m])
  
  (:require [penumbra.app    :as app]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def all-wall-bits (m/get-bits-of-type "wall"))

(defn draw-world2 []
  (nth (iterate recur-geom #(d/cube 1.5 0.5)) 5))

(defn draw-world [t]
  (defn f [v] (u/zero-to-one-cos (* v t)))

  (gl/scale 0.25 0.25 0.25)

  (draw-world2)
  
  (doseq [b all-wall-bits]
    (let [{x :x y :y} b]
      (gl/push-matrix
       (gl/translate x y 0)
       (gl/color (f x) (f y) (f (+ x (f t))))
       (d/cube 1 1)
       )
      )
    )
  )


(defn recur-geom [func]
  (gl/push-matrix
   (gl/scale 0.75 0.75 0.75)
   (gl/rotate 25.5 0 1 0)
   
   (gl/push-matrix
    (gl/translate 0 0 1)
    (func)
    (gl/translate 0 0 -2)
    (func))
   
   #(recur-geom func)))


;;; Scene init, resize handling, updates, input
(defn mouse-scene-rotation [state]
  (gl/rotate (:rot-x state) 1 0 0)
  (gl/rotate (:rot-y state) 0 1 0))


;; Drag the mouse
(defn mouse-drag [[dx dy] [x y] button state]
  (assoc state
    :rot-x (+ (:rot-x state) dy)
    :rot-y (+ (:rot-y state) dx)))

;; Display loop
(defn display-loop [time state]

  (def mul-tab '(10 3.3 5.5 3) )

  (defn mosc [m] (u/zero-to-one-cos (* m time)))

  (let [ [r g b a]  (map mosc mul-tab) ]

    (gl/color r g b a)
    (gl/translate 0 -0.93 -3)
    ;;(gl/rotate (rem (* 20 time) 360) 0 1 0)
    (mouse-scene-rotation state)
    (draw-world time)
    )
  )

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


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Init the App
(defn init [state]
  (app/vsync! true)
  (gl/enable :depth-test)
  (gl/enable :normalize)
  (gl/enable :depth-test)
  (gl/disable :cull-face)
  
  state)

;; Reshape the winodw
(defn reshape [[x y width height] state]
  (gl/frustum-view 60.0 (/ (double width) height) 1.0 100.0)
  (gl/load-identity)
  state)

;; Game display loop
(defn display [[delta t] state]
  (display-loop t state)
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


