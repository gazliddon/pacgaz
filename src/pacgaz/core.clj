(ns pacgaz.core

  (:require [pacgaz.draw     :as d])
  (:require [pacgaz.utils    :as u])
  (:require [pacgaz.mapdata  :as m])
  
  (:require [penumbra.opengl :as gl])
  
  (:require [penumbra.app    :as app]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn it-bits [type func]
  (doseq [b (m/get-bits-of-type type)]
    (func (:x b) (:y b)))
  )

;; Draw items in map
(defn draw-item [func x y]
  (gl/push-matrix
   (gl/translate x y 0)
   (func)))

(defn draw-pill [x y]
  (draw-item #(d/circle 10 0.15) x y))

(defn draw-wall [x y]
  (def p #(d/cube 1 1))
  (draw-item p x y))

;; Silly functions to get a colour from pos and time 

(defn f [t v] (u/zero-to-one-cos (* v t)))

(defn f2 [t x adder divisor] (f t (/ (+ x adder ) divisor)))

(defn pos-to-col [t x y z]
  (list (f2 t x 1 0.7) (f2 t x 1 1.025) (f2 t y 0.5 1.025)))

(defn gl-pos-to-col
  [t x y z] (apply gl/color (pos-to-col t x y 0)))

(defn rgb
  [r g b] (gl/color (/ r 255.0) (/ g 255.0) (/ b 255.0) 1))

(defn draw-world [t]

  (gl/scale 0.2 0.2 0.2)
  (gl/translate -14 -14 0)
  (rgb 255 184 151)
  (it-bits "pill" draw-pill)
  (rgb 4 51 255)
  (it-bits "wall" draw-wall)
  )


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


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

;; Display loop

(defn display-loop [time state]

  (gl/material :front-and-back
               :ambient-and-diffuse [0.75 0.75 0.25 1])
  (gl/light 0
            :position [1 1 1 0])

  (def mul-tab '(10 3.3 5.5 3) )
  (defn mosc [m] (u/zero-to-one-cos (* m time)))

  (let [ [r g b a]  (map mosc mul-tab) ]

    (gl/color r g b a)
    (apply gl/translate (:pos state))

    ;;(gl/rotate (rem (* 20 time) 360) 0 1 0)

    (mouse-scene-rotation state)
    (if (:left state)
      (d/sphere 10 40 1)
      (d/sphere 10 40 2))
    
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
                                        ; (app/size [100 100])
  (app/vsync! true)
  (gl/enable :depth-test)
  (gl/enable :normalize)
  (gl/enable :depth-test)
  (gl/disable :cull-face)
                                        ;  (gl/enable :lighting)
                                        ;  (gl/enable :light0)
                                        ;  (gl/shade-model :smooth)

  state)



;;; Reshape the winodw
(defn reshape [[x y width height] state]
  (gl/frustum-view 60.0 (/ (double width) height) 0.1 1000.0)
  (gl/load-identity)
  state)

;; Drag the mouse
(defn mouse-drag [[dx dy] [x y] button state]
  (assoc state
    :rot-x (+ (:rot-x state) dy)
    :rot-y (+ (:rot-y state) dx)))

(defn key-press [key state]
  (println key)
  (assoc state
    :left true)
  )

(defn key-release [key state]
  (println key)
  (assoc state
    :left false))

;; Game display loop
(defn display [[delta t] state]

  (display-loop t state)
  (app/repaint!))

;; Start the display loop
(defn start-display-loop []

  (let [ state {
                :rot-x 0 :rot-y 0
                :pos [0 0.93 -8]
                :left false
                :right false
                :up false
                :down false
                
                }
        app-options {
                     :display display
                     :reshape reshape
                     :init init
                     :mouse-drag mouse-drag
                     :key-press key-press
                     :key-release key-release
                     }]

    (app/start app-options state)))

(.start (Thread. start-display-loop))


