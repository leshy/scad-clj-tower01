;; http://adereth.github.io/blog/2014/04/09/3d-printing-with-clojure/
;; https://github.com/farrellm/scad-clj/blob/master/src/scad_clj/model.clj

(ns tower01.core
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]))

(def wall 4)
(def sidetube_n 4)
(def sidetube_width 50)
(def sidetube_height 107)
(def connector_height 30)
(def smallhole 10)
  
(def width 300)
(def height 200)

(defn tube [width height]
  (difference
   (cylinder width height)
   (cylinder (- width wall) (+ height wall))
   )
  )

(defn spin_obj [obj total]
  (def step (*(/ pi total) 2))
  
  (defn spinner [current]
    (->>
     obj
     (translate [width 0 0])
     (rotate (* step current) [0 0 1])
     ))
  
  (map spinner (range 0 total))
)

(defn autospin_obj [obj objwidth]
  (spin_obj obj (/ (* 2 pi width) objwidth))
)

(defn autostack_obj [obj objsize]
  (def total 8)
  (defn stacker [current]
    (translate
     [0 0 (* objsize current)]
     (spin_obj obj (/ (* pi 2 width) objsize))
     )
  )
  (map stacker (range 0 total))
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


(def holetube
   (difference
    (tube width height)
    )
)

(defn roto [obj] (rotate (/ pi 4) [0 1 0] obj))

(def tower_base_old
  (union
   (difference
    (side_objs (roto (tube (+ sidetube_width wall) sidetube_height)))
    (cylinder (- width wall) (+ height wall))
    )
   
   (difference
    (tube width height)
    ;; (difference
    (side_objs (roto (cylinder (+ sidetube_width wall) sidetube_height)))
    ;;  (autospin_obj (cube 30 5 200) 15)
    ;;  )
    )

   (translate [ 0 0 (- (/ height 2)) ] connector)
   
   )
  )



(defn cradle [csize]
  

    (translate [(- (* 2 wall)) 0 0 ]
               (difference
                
                (sphere csize)
                (translate [0 0 csize]
                           (cube (* 2 csize) (* 2 csize) (* 3 csize))
                           )
                (sphere (- csize wall))
                (cylinder wall 1000)
                
                )
               )
    )

(def tower_base_old2
  (union
   
   ;; (difference
   ;;  (side_objs (sphere sidetube_width))
   ;;  (cylinder (- width wall) (+ height wall))
   ;;  )
   
   (side_objs (cradle (* 2 sidetube_width)))
   
   (difference
    (tube width height)
    (side_objs (sphere sidetube_width))
    )
   
   (translate [ 0 0 (- (/ height 2)) ] connector)
   
   )
  )


(def bigsphere
  (difference
   (sphere width)
  ))


(def tower_base
  (difference
   
  (union
   (difference
    (sphere width) ;sphere 
    (translate [0 0 width] (cube (* 2 width) (* 2 width) (* 3.75 width))) ;cutoff
    (sphere (- width wall)) ;dig in
    (translate [0 0 (- (- width wall))]
               (cylinder wall 30) ;make a hole
               )
    )
   (translate [0 0 (- (+ width 10))]
   (tube (* wall 2) 25) ;make a hole
   )
   )

  (translate [(/ width 2) (/ width 2) (- (/ height 2))] (cube width width width))
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
   (stacked_bases 3)
   ;; (translate [(/ cubesize 2) (/ cubesize 2) 0]
   ;;            (cube cubesize cubesize cubesize))
   ;; )
  )

(defn -main
  [& args]
  (println "generatin")
  (spit "tower01.scad" (write-scad primitives))
  )

