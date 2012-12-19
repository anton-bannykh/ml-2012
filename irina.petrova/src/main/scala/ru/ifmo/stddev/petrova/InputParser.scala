package main.scala.ru.ifmo.stddev.petrova

import java.io.{BufferedInputStream, File, FileInputStream, DataInputStream}
import org.apache.commons.math3.linear.{Array2DRowRealMatrix, RealMatrix, ArrayRealVector, RealVector}
import collection.mutable.ListBuffer

object InputParser {
  def readImage(is : DataInputStream) = {
    val magic = is.readInt()
    assert(magic == 0x00000803, "magic ok")
    val imageCou = is.readInt()
    val rowCou = is.readInt()
    val colCou = is.readInt()
    Stream.continually(Stream.continually(is.read().toDouble).take(rowCou * colCou).toArray).take(imageCou).toArray
  }

  def readLabels(is : DataInputStream) : RealVector = {
    val magic = is.readInt()
    assert(magic == 0x00000801, "magic ok")
    val itemCou = is.readInt()
    new ArrayRealVector(Stream.continually(is.read().toDouble).take(itemCou).toArray)
  }


  def main(args : Array[String]) {
    val trainImages = readImage(new DataInputStream(new BufferedInputStream(new FileInputStream(new File("train-images.idx3-ubyte"))))).map(x => new ArrayRealVector(x, false).append(1))
    println("nya1")
    val trainLabels = readLabels(new DataInputStream(new BufferedInputStream(new FileInputStream(new File("train-labels.idx1-ubyte")))))
    val testImages = readImage(new DataInputStream(new BufferedInputStream(new FileInputStream(new File("t10k-images.idx3-ubyte"))))).map(x => new ArrayRealVector(x, false).append(1))
    println("nya2")
    val testLabels = readLabels(new DataInputStream(new BufferedInputStream(new FileInputStream(new File("t10k-labels.idx1-ubyte")))))
//    val res = new ListBuffer[RealVector]
//    val perc = new ListBuffer[Perceptron]
//    (0 until 10).foreach(perc += new Perceptron(784, _))
//    (0 until 10).foreach(res += perc(_).learn(trainImages, trainLabels))
//    //res.foreach(println)
//    println(perc(0).learn(trainImages, trainLabels))
//    for (i <- 0 to 9) {Console println perc(i).test(testImages, testLabels, res(i))}
    val perc = new CommonPerceptron(785)
    println(perc.test(testImages, testLabels, perc.learn(trainImages, trainLabels)))
  }
}
