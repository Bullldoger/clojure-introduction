(def color-1 "red")
(def color-2 "blue")

(loop []
  (println "colors?")
  (println "First color")
  (let [user-color-1 (read-line)]
    (println "Second color")
    (let [user-color-2 (read-line) position 0 correct 0]
      (do
        (if (= color-1 user-color-1) 1 0)
        (if (= color-2 user-color-2) 1 0)
        (if (= color-1 user-color-2) 1 0)
        (if (= color-2 user-color-1) 1 0)

        (println "Correct at positions")
        (let [correct_positions (+ (if (= color-1 user-color-1) 1 0) (if (= color-2 user-color-2) 1 0))
              correct_colors (+ (if (= color-1 user-color-2) 1 0) (if (= color-2 user-color-1) 1 0))]

          (println "Continue ?")
          (let [continue (not= (read-line) "no")] (if continue (do (println continue) (recur))))
        )
      )
    )
  )
)