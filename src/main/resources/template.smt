(declare-datatypes () ((Robot Red Blue Green Yellow)))

(declare-datatypes () ((Position (Position (j Int)))))

(declare-datatypes () ((Movement (Movement (orig Position) (dest Position)))))

(declare-datatypes () ((RobotMovement (RobotMovement (robot Robot) (mov Movement)))))


(declare-fun movement (Int) RobotMovement)

(declare-fun position (Robot Int) Position)

(declare-const usedTime Int)
(assert (<= usedTime 20)) ; TODO maybe add this as a variable

(define-fun isPositionFilled ((pos Position) (time Int)) Bool
  (not (forall ((robot Robot)) (not (= (position robot time) pos))))
)

; Time limits
(define-fun isValidTime ((time Int)) Bool (and (>= time 0) (<= time usedTime)))
(assert (isValidTime usedTime))


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

(assert (forall ((robot1 Robot) (robot2 Robot) (time Int))
  (ite (and (not (= robot1 robot2)) (isValidTime time))
    (not (= (position robot1 time) (position robot2 time)))
    true
  )
))


(assert (forall ((robot Robot) (time Int))
  (ite (isValidTime time)
    (ite (and (= (robot (movement time)) robot) (isValidTime time))
    ; if the robot moved then
      (and
        ; the origin of a movement must be the current position of the robot
        (= (orig (mov (movement time))) (position robot time))
        ; and the destination of a movement must be the next position of the robot
        (= (dest (mov (movement time))) (position robot (+ time 1)))
        ; origin and destination should also be different
        ; Since we are minimizing time this condition is not necessary,
        ; because a move with the same origin and destination would be wasting time
        ;(not (= (orig (mov (movement time))) (dest (mov (movement time)))))
      )
      ; if the robot didn't move then
      ; its future position must be the same as the current
      (= (position robot time) (position robot (+ time 1)))
    )
  true
  )
))


; TODO maybe these two could be merged into one
(assert (forall ((time Int))
  (ite (isValidTime time)
    ; if a movement exists then that movement must be possible
    (possibleMovement (mov (movement time)) time)
    true
  )
))

(assert (forall ((time Int))
  (ite (isValidTime time)
    (and
       ; the origin and destination of a movement must be valid
      (isValidPosition (dest (mov (movement time))))
      (isValidPosition (orig (mov (movement time))))
    )
    true
  )
))



;;;;;;;
;; Problem specific
;;;;;;;

; Initial positions
;;;{initialPositions}

; Objective Position
;;;{objectivePosition}

;;;;;;;
;;;;;;;

;;;;
(minimize usedTime)
(check-sat)
;(get-model)
;(get-info :all-statistics)
