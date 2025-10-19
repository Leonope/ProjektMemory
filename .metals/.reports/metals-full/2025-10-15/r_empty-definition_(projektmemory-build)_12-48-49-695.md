error id: file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester5/Web-apps/ProjektMemory/build.sbt:sbt/Keys.libraryDependencies.
file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester5/Web-apps/ProjektMemory/build.sbt
empty definition using pc, found symbol in pc: sbt/Keys.libraryDependencies.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -libraryDependencies.
	 -libraryDependencies#
	 -libraryDependencies().
	 -scala/Predef.libraryDependencies.
	 -scala/Predef.libraryDependencies#
	 -scala/Predef.libraryDependencies().
offset: 436
uri: file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester5/Web-apps/ProjektMemory/build.sbt
text:
```scala
name := "ProjektMemory"

version := "0.1"

scalaVersion := "2.13.12"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % "test"
libraryDependencies += "org.scalamock" %% "scalamock" % "5.1.0" % "test"
libraryDependencies += "org.scalafx" %% "scalafx" % "20.0.0-R31"
libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0"
libraryDependencies += "com.google.inject" % "guice" % "6.0.0"

lib@@raryDependencies += "net.codingwell" %% "scala-guice" % "6.0.0"
libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.0.1"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.4"

// Define the source directories explicitlyy
//Compile / unmanagedSourceDirectories += baseDirectory.value / "src/main/scala"
//Test / unmanagedSourceDirectories += baseDirectory.value / "src/test/scala"

// Assembly merge strategy to handle duplicate files
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x if x.endsWith("module-info.class") => MergeStrategy.discard
  case x if x.startsWith("META-INF/services/") => MergeStrategy.filterDistinctLines
  case x if x.startsWith("META-INF/substrate/config") => MergeStrategy.first
  case x if x.startsWith("META-INF/spring.schemas") => MergeStrategy.first
  case x if x.startsWith("META-INF/spring.handlers") => MergeStrategy.first
  case x if x.endsWith(".html") => MergeStrategy.discard
  case x => MergeStrategy.first
}

// Specify the main class
mainClass in assembly := Some("main.Memory") // Ändern Sie dies auf Ihre Hauptklass



```


#### Short summary: 

empty definition using pc, found symbol in pc: sbt/Keys.libraryDependencies.