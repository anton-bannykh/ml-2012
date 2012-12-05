import java.io.File
import ru.ifmo.ctd.rost.ml.mnistIO

object Test extends App{
  implicit val relative = new File("../data/mnist").toURI .normalize()
  Console println("done!! ")
}
