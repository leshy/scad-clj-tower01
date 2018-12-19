;; http://adereth.github.io/blog/2014/04/09/3d-printing-with-clojure/
;; https://github.com/farrellm/scad-clj/blob/master/src/scad_clj/model.clj

(ns tower01.core
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]))

(def wall 4)
(def sidetube_n 3)
(def sidetube_width 50)
(def sidetube_height 100)
(def connector_height 30)

(def width 70)
(def height 120)

(defn tube [width height]
  (difference
   (cylinder width height)
   (cylinder (- width wall) (+ height wall))
   )
  )

(defn side_obj [obj n]
  (->>
   obj
   (translate [(- width (/ wall 2)) 0 wall])
   (rotate (* (/ (* 2 pi) sidetube_n) n) [0 0 1])
 )
)

(defn side_objs [obj]
  (union
   (map (partial side_obj obj) (range 1 (+ sidetube_n 1))))
  )

(def connector
  (difference
    (cylinder (- width wall) connector_height)
    (cylinder (- width (* 2 wall)) (+ connector_height 1))
    
   
    (translate [0 0 (+ (/ connector_height 2) 0.1)]
               (cylinder [(- width (* 2 wall)) width] connector_height))
    )
  )

(defn cradle [csize]
    (translate [(- (* 2 wall)) 0 0 ]
               (difference
                
                (sphere csize)
                (translate [0 0 csize]
                           (cube (* 2 csize) (* 2 csize) (* 2 csize))
                           )
                (sphere (- csize wall))
                (cylinder wall 1000)
                
                )
               )
    )

(def tower_base
  (union
   
   ;; (difference
   ;;  (side_objs (sphere sidetube_width))
   ;;  (cylinder (- width wall) (+ height wall))
   ;;  )
   
   (side_objs (cradle  sidetube_width))
   
   (difference
    (tube width height)
    (side_objs (sphere sidetube_width))
    )
   
   (translate [ 0 0 (- (/ height 2)) ] connector)
   
   )
  )

(defn translated_base [n]
  (let [angle (* (mod n 2) (/ pi sidetube_n))]
    (->>
     tower_base
     (translate [0 0 (* height n)])
     (rotate angle [0 0 1])
    )
    )
  )

(defn stacked_bases [n]
  (translate
   [0 0 (+(-(* (/ n 2) height)) (/ height 2)) ]
   (union
    (map translated_base (range 0 n)
         ))
   )
  )

(def cubesize 200)

(def primitives
;;  (difference
   (stacked_bases 6)
   ;; (translate [(/ cubesize 2) (/ cubesize 2) 0]
   ;;            (cube cubesize cubesize cubesize))
   ;; )
  )

(defn -main
  [& args]
  (println "generatin")
  (spit "tower01.scad" (write-scad primitives))
  )
