name := "machine_learning"

version := "0.1"

scalaVersion := "2.9.2"

libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "1.8" % "test",
                            "junit" % "junit" % "4.10" % "test",
                            "org.apache.commons" % "commons-math3" % "3.0",
                            "com.googlecode.efficient-java-matrix-library" % "ejml" % "0.21")
