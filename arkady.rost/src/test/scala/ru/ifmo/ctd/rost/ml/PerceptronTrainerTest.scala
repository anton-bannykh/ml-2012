package ru.ifmo.ctd.rost.ml

class PerceptronTrainerTest extends MnistClassifierTrainerTest {
  def trainer: Trainer[Array[Double], Int] = new Perceptron.Trainer(100)
}
