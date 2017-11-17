(define-fun isPositionFilled (
    (pos Int)
    (positionRed Int)
    (positionYellow Int)
    (positionGreen Int)
    (positionBlue Int)) Bool
  (or
    (= positionRed pos)
    (= positionYellow pos)
    (= positionGreen pos)
    (= positionBlue pos)
  )
)

;;;;;;;
;; Problem specific conditions
;;;;;;;

(define-fun possibleMovement (
    (orig Int)
    (dest Int)
    (positionRed Int)
    (positionYellow Int)
    (positionGreen Int)
    (positionBlue Int)) Bool
  (or
    (= orig dest)
    ;;;{possibleMovements}
  )
)

; position limits
(define-const MAX_POS Int
  ;;;{maxPosition}
)
(define-fun isValidPosition ((pos Int)) Bool (and (>= pos 1) (<= pos MAX_POS)))

;;;;;;;
;;;;;;;

(define-fun arePositionsAndMoveValid (
    (robot Int)
    (position Int)
    (positionPlusOne Int)
    (orig Int)
    (dest Int)
    (moveRobot Int)) Bool
  (ite (= moveRobot robot)
    (and
      (= orig position)
      (= dest positionPlusOne)
    )
    (= position positionPlusOne)
  )
)

(declare-const positionRed0 Int)
(declare-const positionBlue0 Int)
(declare-const positionGreen0 Int)
(declare-const positionYellow0 Int)


;;;;;;;
;; Problem specific
;;;;;;;

; Initial positions
;;;{initialPositions}


;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; Steps
;;;;;;;;;;;;;;;;;;;;;;;;;;
