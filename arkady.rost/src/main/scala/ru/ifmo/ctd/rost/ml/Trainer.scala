package ru.ifmo.ctd.rost.ml


trait Trainer[T, L] {
  def train(data : Iterable[T], labels : Iterable[L]) : T => L
}
