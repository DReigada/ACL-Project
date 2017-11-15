(set-option :smt.mbqi true)

(declare-datatypes () ((Robot Red Blue Green Yellow)))

(declare-datatypes () ((Position (Position (j Int)))))

(declare-datatypes () ((Movement (Movement (orig Position) (dest Position)))))

(declare-datatypes () ((RobotMovement (RobotMovement (robot Robot) (mov Movement)))))


;(declare-const usedTime Int)
;(assert (>= usedTime 0))
;(assert (<= usedTime 20)) ; TODO maybe add this as a variable

; Time limits
;(define-fun isValidTime ((time Int)) Bool (and (>= time 0) (<= time usedTime)))
;(assert (isValidTime usedTime))

(declare-fun position (Robot Int) Position)

(define-fun isPositionFilled ((pos Position) (time Int)) Bool
  (or
    (= (position Red time) pos)
    (= (position Yellow time) pos)
    (= (position Green time) pos)
    (= (position Blue time) pos)
  )

  ;(exists ((robot Robot)) (= (position robot time) pos))
  ;(not (forall ((robot Robot)) (not (= (position robot time) pos))))
)



;;;;;;;
;; Problem specific conditions
;;;;;;;

(define-fun possibleMovement ((mov Movement) (time Int)) Bool
  (or
    (= (orig mov) (dest mov))
    ;;;{possibleMovements}
  )
)

; position limits
(define-const MAX_POS Int
  ;;;{maxPosition}
)
(define-fun isValidPosition ((pos Position)) Bool (and (>= (j pos) 1) (<= (j pos) MAX_POS)))

;;;;;;;
;;;;;;;

(define-fun bla ((robot Robot) (t0 Int) (t1 Int) (movement RobotMovement)) Bool
  (ite (= (robot movement) robot)
    (and
      (= (orig (mov movement)) (position robot t0))
      (= (dest (mov movement)) (position robot t1))
    )
    (= (position robot t0) (position robot t1))
  )
)

;;;;;;;
;; Problem specific
;;;;;;;

; Initial positions
;;;{initialPositions}


;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; Steps
;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;
