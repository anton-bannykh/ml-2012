package ru.ifmo.ctd.rost.ml

import java.io.{EOFException, BufferedInputStream, InputStream}


class MnistInputStream(stream : InputStream) extends BufferedInputStream(stream) {
  val bb = new Array[Byte](4)
  def readInt : Int = {
    if (read(bb, 0, 4) < 4) throw new EOFException()
    bb(0) << 24 | (bb(1) & 0xFF) << 16 | (bb(2) & 0xFF) << 8 | (bb(3) & 0xFF)
  }
}

trait MnistReader[T] {
  def parse(stream : MnistInputStream) : T
}

object IDX1Reader extends MnistReader[Stream[Int]] {
  def parse(stream: MnistInputStream) = {
    val rMagic = stream.readInt
    assert(rMagic == 0x00000801, "magic number 0x00000801")
    val size = stream.readInt
    Stream.continually(stream.read).take(size)
  }
}

object IDX3Reader extends MnistReader[Stream[Array[Int]]] {
  def parse(stream: MnistInputStream) = {
    val rMagic = stream.readInt
    assert(rMagic == 0x00000803, "magic number 0x00000803")
    val size = stream.readInt
    val rows = stream.readInt
    val columns = stream.readInt
    def readFrame =  Stream.continually(stream.read).take(rows * columns).toArray
    Stream.continually(readFrame).take(size)
  }
}
