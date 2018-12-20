;; http://adereth.github.io/blog/2014/04/09/3d-printing-with-clojure/
;; https://github.com/farrellm/scad-clj/blob/master/src/scad_clj/model.clj
;;
;; lein auto run
;;
;; in the .scad buffer:
;; auto-revert-mode 
;; scad-preview-mode

(ns tower01.core
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]))

(def wall 4)
(def sidetube_n 5)
(def sidetube_width 50)
(def sidetube_height 100)
(def connector_height 30)

(def width 100)
(def height 160)

(defn tube [width height]
  (difference
   (cylinder width height)
   (cylinder (- width wall) (+ height wall))
   )
  )

(defn side_obj [obj n]
  (->>
   obj
   (rotate (/ 3.14 4) [0 1 0])
   (translate [width 0 0])
   (rotate (* (/ (* 2 3.14) sidetube_n) n) [0 0 1])
 )
)

(defn side_objs [obj]
  (union
   (map (partial side_obj obj) (range 1 (+ sidetube_n 1))))
  )

(def connector
  
  (let [ bottom_space (/ (- height (* sidetube_width 1.5)) 2) ]  ;; calculate how much space we have under the tube
    (translate [0 0 0] 
               (union
                (difference

                 ;; (union
                 (cylinder (- width wall) (* connector_height 2))

                 ;; (translate [0 0 (/ connector_height -2)]
                 ;;            (cylinder (+ width wall) (/ connector_height 2)))
                 ;; )

                 (cylinder (- width (* 2 wall)) (+ (* connector_height 2) 1))

                 ;; (translate [0 0 (+ connector_height 0.1)]
                 ;;            (cylinder [(- width (* 2 wall)) width] connector_height))

                 )
                )
               )
     )
    
  )

(def tower_base
  (union
   
   (difference
    (side_objs (tube sidetube_width sidetube_height))
    (cylinder (- width wall) (+ height wall))
   )
   
   (difference
    (tube width height)
    (side_objs (cylinder sidetube_width sidetube_height))
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
   (stacked_bases 1)
   ;; (translate [(/ cubesize 2) (/ cubesize 2) 0]
   ;;            (cube cubesize cubesize cubesize))
   ;; )
  )

(defn -main
  [& args]
  (println "generatin")
  (spit "tower01.scad" (write-scad primitives))
  )
