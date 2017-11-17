;;;;;;;;;;;;;
;; Movement $0
;;;;;;;;;;;;;
(push)
(declare-const movementOrig$0 Int)
(declare-const movementDest$0 Int)
(declare-const movementRobot$0 Int)


(declare-const positionRed$1 Int)
(declare-const positionBlue$1 Int)
(declare-const positionGreen$1 Int)
(declare-const positionYellow$1 Int)

(assert (possibleMovement movementOrig$0 movementDest$0 positionRed$0 positionYellow$0 positionGreen$0 positionBlue$0))
(assert
  (and
    (arePositionsAndMoveValid 0 positionRed$0 positionRed$1 movementOrig$0 movementDest$0 movementRobot$0)
    (arePositionsAndMoveValid 1 positionYellow$0 positionYellow$1 movementOrig$0 movementDest$0 movementRobot$0)
    (arePositionsAndMoveValid 3 positionGreen$0 positionGreen$1 movementOrig$0 movementDest$0 movementRobot$0)
    (arePositionsAndMoveValid 2 positionBlue$0 positionBlue$1 movementOrig$0 movementDest$0 movementRobot$0)
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
