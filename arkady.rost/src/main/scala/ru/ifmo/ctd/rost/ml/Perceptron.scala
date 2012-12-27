package ru.ifmo.ctd.rost.ml

import org.apache.commons.math3.linear.{Array2DRowRealMatrix, ArrayRealVector, RealVector}

object Perceptron {
  class Trainer(maxPasses: Int = 20) extends ru.ifmo.ctd.rost.ml.Trainer[Array[Double], Int] {

    implicit def wrapToVec(arr : Array[Double]) : RealVector = new ArrayRealVector(arr, false)
    def train(data: Iterable[Array[Double]], labels: Iterable[Int]): (Array[Double]) => Int = {
      val pairs = data zip labels
      val theta = new Array2DRowRealMatrix(28 * 28, 10)
      //for (i <- 0 until theta.getRowDimension; j <- 0 until theta.getColumnDimension) theta.addToEntry(i, j, 1)
      def improve(vec : RealVector, label : Int) {
        val scalar = theta.preMultiply(vec)
        val argMax = scalar.getMaxIndex
        if (argMax != label) {
          for (i <- 0 until theta.getRowDimension) {
            theta.addToEntry(i, argMax, -vec.getEntry(i))
            theta.addToEntry(i, label, vec.getEntry(i))
          }
        }
      }
      (1 to maxPasses) foreach { i => {
        println("Step: " + i)
        pairs foreach(p =>   improve(p._1, p._2))
      }}
      vec : Array[Double] => theta.preMultiply(vec).getMaxIndex
    }
  }
}
