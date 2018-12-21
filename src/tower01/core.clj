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


;; settings
(def wall 4)
(def sidetube_n 4)
(def sidetube_d 50)
(def sidetube_spacing 25)

(def connector_height (* wall 2))




;; sidetube settings imply main element diameter
;; here we calculate the radius
(def sidetube_r (/ sidetube_d 2))
(def circumvence (* (+ sidetube_r (/ sidetube_spacing 2)) 2 sidetube_n))
(def diameter (/ circumvence Math/PI))
(def radius (/ diameter 2))

;; here we calculate the height, implied by cut height
(def sidetube_cuth (* sidetube_d 1.5))
(def sidetube_cuth_external (+ sidetube_cuth (* wall 2)))
(def sidetube_cutw sidetube_r)
(def height sidetube_cuth)

;; here we calculate how long the sidetube should be in order to fully protrude from the main element
(def sidetube_length (- (* (/ height 2) (Math/sqrt 2)) (/ wall 2)))

(println "element diameter" radius)
(println "element height" height)
(println "side tube diameter" sidetube_d)
(println "side tube length" sidetube_length)
(println "side tube cut height" sidetube_cuth)


(defn tube [radius height]
  (difference
   (cylinder radius height)
   (cylinder (- radius wall) (+ height wall))
   )
  )

(defn side_obj [obj n]
  (->>
   obj
   (translate [radius 0 0])
   (rotate (* (/ (* 2 Math/PI) sidetube_n) n) [0 0 1])
   )
  )

(defn side_objs [obj]
  (union
   (map (partial side_obj obj) (range 1 (+ sidetube_n 1))))
  )


(def connector_cuts
              (side_objs
              (rotate (/ Math/PI 2) [0 1 0]
              (rotate (/ Math/PI 4) [0 0 1]
                      
                      (translate [0 0 (/ radius -2)]
                                 (with-fn 4 (cylinder [0 (/ radius 1.25)] radius)))
                      ))
              )
              )
  

(def connector_top
   
   ;; (translate [0 0 (/ connector_height -2)]
   ;;            (tube radius connector_height))
   
   (translate [0 0 (* connector_height -0.25)]
              (difference
               (tube (- radius wall) (/ connector_height 2))


               (rotate (/ Math/PI 4) [0 0 1]

               connector_cuts
              )))
  )


(def connector_bottom
  (difference
  (union
   ;; base
   
   (difference

    (union
     (translate [0 0 (+ (/ connector_height -2) (/ wall 2))]
                (tube (- radius (* wall 2)) (+ connector_height wall)))
     
     (translate [0 0 (/ wall 2)]
                (tube (- radius wall) wall))
     )

    ;; angle
    (translate [0 0 (/ wall 2)]
               (cylinder [(- radius (* wall 3)) (- radius wall)] (+ wall 0.001)))
    
    )

   ;; ;; tooth
   (translate [0 0 (* wall -1.5)]
               (tube (- radius wall) wall)
              )
   )


  connector_cuts
  )
  )


(def connector
 (color [1,0,0,0.5]
         (union
          (translate
           [0 0 (/ height +2)] connector_top)
          (translate
           [0 0 (/ height -2)] connector_bottom)
         )
  )
)
  

(def tower_base
  (union
      

  (color [1,1,1,0.5]
          (difference
           (side_objs (rotate (/ Math/PI 4) [0 1 0] (tube sidetube_r sidetube_length)))
           (cylinder (- radius wall) (+ height wall)))
          (difference
           (tube radius height)
           (side_objs (rotate (/ Math/PI 4) [0 1 0] (cylinder sidetube_r sidetube_length))))
         )

   connector
   
   )
  )

(defn translated_base [explode n]
  (let [angle (* (mod n 2) (/ pi sidetube_n))]
    (->>
    (color [(mod n 2),(- 1 (mod n 2)),1,1]
           tower_base
           )
     (translate [0 0 (* (+ height explode) n)])
     (rotate angle [0 0 1])
    )
    )
  )

(defn stacked_bases [n explode]
  (translate
   [0 0 (+
         (- (* (/ n 2) height))
         (/ height 2)) ]
   
   (union (map (partial translated_base explode) (range 0 n)))
   )
  )

(def cubesize (* height 2))

(def primitives
  (difference
   (stacked_bases 3 (* wall 3))
   (translate [(/ cubesize 2) (/ cubesize 2) 0]
              (color [1 0 0 1] (cube cubesize cubesize cubesize)))
  )
  )

(defn -main
  [& args]
  (println "generatin")
  (spit "tower01.scad" (write-scad primitives))
  )
