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
(def connector_height 30)

(def radius 100)

(def circumvence (* 2 radius Math/PI))


(def sidetube_n 5)



;;(def sidetube_radius 50)
;;(def sidetube_height 100)

;; sidetube radius and height are implied by height, sidetube_n and sidetube_spacing
(def sidetube_spacing wall)
(def sidetube_radius (/ (- circumvence (* sidetube_n sidetube_spacing)) sidetube_n 2))

(def sidetube_cuth (* sidetube_radius 1.5))
(def sidetube_cuth_external (+ sidetube_cuth (* wall 2)))
(def sidetube_cutw sidetube_radius)

(def height (* (* sidetube_radius 2) 1.5))

(def sidetube_length (- (* (/ height 2) (Math/sqrt 2)) (/ wall 2)))

(println "tube diameter" sidetube_radius)
(println "tube length" sidetube_length)
(println "tube cut height" sidetube_cuth)
(println "tube height external" sidetube_cuth_external)


(defn tube [radius height]
  (difference
   (cylinder radius height)
   (cylinder (- radius wall) (+ height wall))
   )
  )

(defn side_obj [obj n]
  (->>
   obj
   (rotate (/ 3.14 2) [0 1 0])
   (translate [radius 0 0])
   (rotate (* (/ (* 2 3.14) sidetube_n) n) [0 0 1])
 )
)

(defn side_objs [obj]
  (union
   (map (partial side_obj obj) (range 1 (+ sidetube_n 1))))
  )

(def connector
  
  (let [ bottom_space (/ (- height sidetube_cuth) 2) ]  ;; calculate how much space we have under the tube
    (translate [0 0 0]
               (union
                (difference

                 ;; (union
                 (cylinder (- radius wall) (* connector_height 2))

                 ;; (translate [0 0 (/ connector_height -2)]
                 ;;            (cylinder (+ radius wall) (/ connector_height 2)))
                 ;; )

                 (cylinder (- radius (* 2 wall)) (+ (* connector_height 2) 1))

                 ;; (translate [0 0 (+ connector_height 0.1)]
                 ;;            (cylinder [(- radius (* 2 wall)) radius] connector_height))

                 )
                )
               )
     )
    
  )

(def tower_base
  (union
   
   (difference
    (side_objs (tube sidetube_radius sidetube_length))
    (cylinder (- radius wall) (+ height wall))
   )
   
   (difference
    (tube radius height)
    (side_objs (cylinder sidetube_radius sidetube_length))
   )

;;   (translate [ 0 0 (- (/ height 2)) ] connector)
   
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
