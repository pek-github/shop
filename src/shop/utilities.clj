(ns shop.utilities)

(defn convert-to-integer
  "converts the given parameter to an integer number;
  a nil parameter is not converted"
  [param]
  (if (nil? param)
    nil
    (Integer/parseInt param)))

(defn random-number
  "a uniform random number generator,
   generating integer numbers in range [low, high]"
  [low high]
  (let [delta (- (inc high) low)
        u (rand-int delta)]
    (+ u low)))

(defn sleep
  "makes the current thread to sleep for the given number of seconds"
  [seconds]
  (Thread/sleep (* 1000 seconds)))

