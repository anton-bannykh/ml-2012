package ru.ifmo.ctd.rost.ml

import java.io._
import java.net.URI
import java.util.zip.GZIPInputStream

object mnistIO {
  def t10k_images(implicit relative:URI) = IDX3Reader from relative.resolve("t10k-images-idx3-ubyte.gz")
  def t10k_labels(implicit relative:URI) = IDX1Reader from relative.resolve("t10k-labels-idx1-ubyte.gz")
  def train_images(implicit relative:URI) = IDX3Reader from relative.resolve("train-images-idx3-ubyte.gz")
  def train_labels(implicit relative:URI) = IDX1Reader from relative.resolve("train-labels-idx1-ubyte.gz")
}

class MnistInputStream(stream : InputStream) extends DataInputStream(new BufferedInputStream(stream))

trait MnistReader {
  protected def autoClose[T](in : MnistInputStream, f : MnistInputStream => T) : T = {
    try { f(in) }  finally {  in close()  }
  }
}


object IDX1Reader extends MnistReader {
  def read (in : MnistInputStream)= {
    val rMagic = in.readInt
    assert(rMagic == 0x00000801, "magic number 0x00000801")
    val size = in.readInt
    Stream.continually(in.read()).take(size).toArray
  }

  def from(u : URI) : Array[Int] = {
    val stream = new GZIPInputStream(u.toURL.openStream)
    autoClose(new MnistInputStream(stream), read)
  }
}

object IDX3Reader extends MnistReader{
  def read(stream : MnistInputStream) = {
    val rMagic = stream.readInt
    assert(rMagic == 0x00000803, "magic number 0x00000803")
    val size = stream.readInt
    val rows = stream.readInt
    val columns = stream.readInt
    def readFrame =  Stream.continually(stream.read().toDouble).take(rows * columns).toArray
    Stream.continually(readFrame).take(size).toArray
  }

  def from(u : URI) : Array[Array[Double]] = {
    val in = new GZIPInputStream(u.toURL.openStream)
    autoClose(new MnistInputStream(in), read)
  }
}
