package ru.ifmo.ctd.rost.ml

import java.io.{EOFException, BufferedInputStream, InputStream}
import java.net.URI
import java.util.zip.GZIPInputStream

object mnistIO {
  private[this] implicit def wrapURI(u : URI) : GZIPInputStream = new GZIPInputStream(u.toURL.openStream)

  def t10k_images(implicit relative:URI) = IDX3Reader from relative.resolve("t10k-images-idx3-ubyte.gz")
  def t10k_labels(implicit relative:URI) = IDX1Reader from relative.resolve("t10k-labels-idx1-ubyte.gz")
  def train_images(implicit relative:URI) = IDX3Reader from relative.resolve("train-images-idx3-ubyte.gz")
  def train_labels(implicit relative:URI) = IDX1Reader from relative.resolve("train-labels-idx1-ubyte.gz")
}


class MnistInputStream(stream : InputStream) extends BufferedInputStream(stream) {
  val bb = new Array[Byte](4)
  def readInt : Int = {
    if (read(bb, 0, 4) < 4) throw new EOFException()
    bb(0) << 24 | (bb(1) & 0xFF) << 16 | (bb(2) & 0xFF) << 8 | (bb(3) & 0xFF)
  }
}

class IDX1Reader private(in : MnistInputStream, private[this] var count : Int) extends Iterator[Int] {
  def hasNext = count > 0

  def next() = {
    count = count - 1
    in read()
  }
}

object IDX1Reader {
  def from(stream : InputStream) : IDX1Reader = {
    val in = new MnistInputStream(stream)
    val rMagic = in.readInt
    assert(rMagic == 0x00000801, "magic number 0x00000801")
    new IDX1Reader(in, in.readInt)
  }
}

class IDX3Reader private(in : MnistInputStream, n : Int, m : Int, private[this] var count : Int)  extends Iterator[Image] {
  def hasNext = count > 0

  def next() = {
    count = count - 1
    Image.newBuilder(n, m).fromStream(in).build
  }
}

object IDX3Reader {
  def from(in: InputStream) : IDX3Reader = {
    val stream = new MnistInputStream(in)
    val rMagic = stream.readInt
    assert(rMagic == 0x00000803, "magic number 0x00000803")
    val size = stream.readInt
    val rows = stream.readInt
    val columns = stream.readInt
    new IDX3Reader(stream, rows, columns, size)
  }
}
