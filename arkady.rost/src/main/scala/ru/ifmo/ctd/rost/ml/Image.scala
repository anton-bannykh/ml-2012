package ru.ifmo.ctd.rost.ml

import java.io.InputStream

class Image private(val rows: Int, val columns: Int, val data: Array[Int])

object Image {
  abstract class TRUE
  abstract class FALSE

  def newBuilder(rows : Int, columns : Int) = ImageBuilder.empty(rows, columns)

  class ImageBuilder[HD] private(val n: Int, val m: Int, val data : Option[Array[Int]]) {

    def fromStream(in: InputStream) = new ImageBuilder[TRUE](n, m, Some(Stream.continually(in.read).take(n * m).toArray))
  }

  implicit def enableBuild(builder : ImageBuilder[TRUE]) = new {
    def build() =  new Image(builder.n, builder.m, builder.data.get)
  }

  object ImageBuilder {
    def empty(n : Int, m : Int) = {
      assert(n > 0)
      assert(m > 0)
      new ImageBuilder[FALSE](n, m, None)
    }
  }
}
