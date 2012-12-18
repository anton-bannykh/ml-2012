package ru.ifmo.ctd.rost.ml

import org.scalatest.FunSuite
import java.io.File

trait ClassifierTrainerTest[T, L] extends FunSuite {
  implicit val relative = new File("../data/mnist").toURI.normalize()
  def trainer: Trainer[T, L]
  def stats(data : Iterable[T], labels : Iterable[L], classifier : T => L) : Double = {
    val stats = (data zip labels).foldLeft((0, 0)) ((acc, p) => {
      if (classifier(p._1) == p._2) {
        (acc._1 + 1, acc._2)
      } else { (acc._1, acc._2 + 1)}
    })
    stats._1.toDouble / (stats._1 + stats._2)
  }
}

trait MnistClassifierTrainerTest extends ClassifierTrainerTest[Array[Double], Int] {
  test("mnist") {
    val classifier = trainer.train(mnistIO.train_images.toIterable, mnistIO.train_labels.toIterable)
    val success = stats(mnistIO.t10k_images.toIterable, mnistIO.t10k_labels.toIterable, classifier)
    println(success)
    //assume(success > 0.65)
  }
}

