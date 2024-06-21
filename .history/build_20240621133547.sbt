name := "ProjektMemory"

version := "0.1"

scalaVersion := "2.13.12"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % "test"
libraryDependencies += "org.scalamock" %% "scalamock" % "5.1.0" % "test"
libraryDependencies += "org.scalafx" %% "scalafx" % "20.0.0-R31"
libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0"
libraryDependencies += "com.google.inject" % "guice" % "6.0.0"

// Define the source directories explicitlyy
//Compile / unmanagedSourceDirectories += baseDirectory.value / "src/main/scala"
//Test / unmanagedSourceDirectories += baseDirectory.value / "src/test/scala"

