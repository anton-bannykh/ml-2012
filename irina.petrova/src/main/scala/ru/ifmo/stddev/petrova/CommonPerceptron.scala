package main.scala.ru.ifmo.stddev.petrova

import org.apache.commons.math3.linear.{ArrayRealVector, Array2DRowRealMatrix, RealVector}
import collection.mutable.ListBuffer

class CommonPerceptron(len : Int) {
  val iterCou = 1
  def learn(images : Array[RealVector], labels : RealVector) : ListBuffer[RealVector] = {
    val teta = new ListBuffer[RealVector]
    for (i <- 0 until 10) {
      teta += new ArrayRealVector(len, 0)
    }
    def imageProc(image : RealVector, label : Int) {
        val scal = teta.map(t => t.dotProduct(image))
        //println(scal)
        val max = scal.toSeq.max
        //println(max)
        val maxInd = scal.findIndexOf(x => x == max)
        if (maxInd != label) {
            //print("atata")

            for (i <- 0 until image.getDimension) {
              teta.apply(maxInd).addToEntry(i, -image.getEntry(i))
              teta.apply(label).addToEntry(i, image.getEntry(i))
            }
        }
    }
    for (i <- 0 until iterCou) {
      (images.zipWithIndex).foreach(im => imageProc(im._1, labels.getEntry(im._2).toInt))
    }
    teta.foreach(println(_))
    teta
  }

  def test(images : Array[RealVector], labels : RealVector, teta : ListBuffer[RealVector]) : Double = {
    def imageProc(image : RealVector, label : Int) : Int = {
      val scal = teta.map(t => t.dotProduct(image))
      val max = scal.toSeq.max
      if (max != scal.apply(label)) 0 else 1
    }
    val res = (images.zipWithIndex).foldLeft(0) ((acc, im) => acc + imageProc(im._1, labels.getEntry(im._2).toInt))
    res.toDouble / images.size
  }

}
