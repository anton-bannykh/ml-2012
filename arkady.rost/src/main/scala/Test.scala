import ru.ifmo.ctd.rost.ml.{IDX3Reader, MnistInputStream, IDX1Reader}

object Test extends App{
  var iter = IDX3Reader.parse(new MnistInputStream(System.in))
  Console.println(iter.length)
  Console println("parsed")
}
