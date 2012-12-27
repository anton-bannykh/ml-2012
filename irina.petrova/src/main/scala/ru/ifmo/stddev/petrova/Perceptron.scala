package main.scala.ru.ifmo.stddev.petrova

import org.apache.commons.math3.linear._


class Perceptron(len : Int, num : Int) {
  val iterCou = 10

  def learn(images : Array[RealVector], labels : RealVector) : RealVector = {
    def iteration(t : RealVector) : RealVector = {
      def imageProc(x : RealVector, t : RealVector, testNum : Int) : RealVector = {
        def improve(y : Double) : RealVector = {
            t.add(x.mapMultiply(y))
        }
        val y = if (labels.getEntry(testNum) == num) 1 else -1
        //if (testNum == 7) {println(t); println(x.dotProduct(t)); println(y) }
        if (x.dotProduct(t) * y < 0) {improve(y)} else t
      }
      //println("nya3")
      (0 until images.size).foldLeft(t) ((a, testNum) => imageProc(images(testNum), a, testNum))
    }
    val t = new ArrayRealVector (len + 1, 1)
    (0 until iterCou).foldLeft(t : RealVector) ((x, y) => iteration(x))

  }

  def test(images : Array[RealVector], labels : RealVector, teta : RealVector) : Double = {
    def imageProc(x : RealVector, testNum : Int) : Int = {
      val y = if (labels.getEntry(testNum) == num) 1 else -1
      if (x.dotProduct(teta) * y < 0) 0 else 1
    }
    val res = (0 until images.size).foldLeft(0) ((acc, testNum) => acc + imageProc(images(testNum), testNum))
    res.toDouble / images.size
  }
}
