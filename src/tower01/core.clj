;; http://adereth.github.io/blog/2014/04/09/3d-printing-with-clojure/

(ns tower01.core
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]))

(def wall 3)
(def sidetube_n 5)
(def sidetube_width 40)
(def sidetube_height 100)

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

(def primitives
  (stacked_bases 3)
  )

(defn -main
  [& args]
  (println "generatin")
  (spit "tower01.scad" (write-scad primitives))
  )
