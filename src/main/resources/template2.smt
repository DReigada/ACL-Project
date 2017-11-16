;;;;;;;;;;;;;
;; Movement $0
;;;;;;;;;;;;;
(push)
(declare-const movement$0 RobotMovement)
(assert (possibleMovement (mov movement$0) $0))
(assert
  (and
    (bla Red $0 $1 movement$0)
    (bla Yellow $0 $1 movement$0)
    (bla Green $0 $1 movement$0)
    (bla Blue $0 $1 movement$0)
  )
)
(assert
  (and
    (isValidPosition (position Red $1))
    (isValidPosition (position Yellow $1))
    (isValidPosition (position Green $1))
    (isValidPosition (position Blue $1))
  )
)
(assert
  (distinct
    (position Red $1)
    (position Blue $1)
    (position Green $1)
    (position Yellow $1)
  )
)


;;;;;;;;;;;;;
;;;;;;;;;;;;;
