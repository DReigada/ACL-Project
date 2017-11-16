;;;;;;;;;;;;;
;; Movement $0
;;;;;;;;;;;;;
(push)
(declare-const movement$0 RobotMovement)
(declare-const positionRed$1 Int)
(declare-const positionBlue$1 Int)
(declare-const positionGreen$1 Int)
(declare-const positionYellow$1 Int)

(assert (possibleMovement (mov movement$0) positionRed$0 positionYellow$0 positionGreen$0 positionBlue$0))
(assert
  (and
    (arePositionsAndMoveValid 0 positionRed$0 positionRed$1 movement$0)
    (arePositionsAndMoveValid 1 positionYellow$0 positionYellow$1 movement$0)
    (arePositionsAndMoveValid 3 positionGreen$0 positionGreen$1 movement$0)
    (arePositionsAndMoveValid 2 positionBlue$0 positionBlue$1 movement$0)
  )
)
(assert
  (and
    (isValidPosition positionRed$1)
    (isValidPosition positionYellow$1)
    (isValidPosition positionGreen$1)
    (isValidPosition positionBlue$1)
  )
)
(assert
  (distinct
    positionRed$1
    positionBlue$1
    positionGreen$1
    positionYellow$1
  )
)


;;;;;;;;;;;;;
;;;;;;;;;;;;;
