package ru.ifmo.ctd.rost.ml

import org.apache.commons.math3.linear.{ArrayRealVector, RealVector}
import java.io.File

object Perceptron {
  implicit val relative = new File("../data/mnist").toURI.normalize()
  def extend(vec : RealVector) = vec.append(1.0)

  def learn(n : Int) : RealVector = {
    val theta = new ArrayRealVector(28 * 28 + 1, 1)

    val pairs = mnistIO.train_images.map(x => new ArrayRealVector(x, false).append(1)) zip mnistIO.train_labels

    //improve
    def iteration(theta : RealVector) : RealVector = {
      pairs.foldLeft(theta)((acc , p ) => {
        val scalar = acc.dotProduct(p._1)
        val sign = if (p._2.toInt == n) 1 else -1
        if (scalar * sign < 0) acc.add(p._1.mapMultiply(sign)) else acc
      })
    }

    //learning iterations
    (0 until 10).foldLeft(theta: RealVector) ((acc, _) => {println(acc);iteration(acc)})
  }

  def test(theta : RealVector, n : Int) : Double = {
    val pairs = mnistIO.t10k_images.map(x => new ArrayRealVector(x, false).append(1)) zip mnistIO.t10k_labels
    pairs.foldLeft(0)((acc, p) => {
      val sign = if (p._2.toInt == n) 1 else -1
      if (theta.dotProduct(p._1) * sign > 0) acc + 1 else acc
    }) / 10000.0
  }

   def main(args : Array[String]) {
    val theta = learn(0)
     println(theta)
     println(test(theta, 0))
  }
}
