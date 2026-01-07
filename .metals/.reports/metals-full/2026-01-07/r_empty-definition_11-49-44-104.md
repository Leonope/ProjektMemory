error id: file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester5/Web-apps/play-server/ProjektMemory/build.sbt:`<none>`.
file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester5/Web-apps/play-server/ProjektMemory/build.sbt
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -project.
	 -project#
	 -project().
	 -scala/Predef.project.
	 -scala/Predef.project#
	 -scala/Predef.project().
offset: 102
uri: file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester5/Web-apps/play-server/ProjektMemory/build.sbt
text:
```scala
name := """web-tui"""
organization := "com.leo"

version := "1.0-SNAPSHOT"

lazy val root = (proj@@ect in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.17"

// Dependencies
libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.2" % Test,
  "org.scala-lang.modules" %% "scala-swing"        % "3.0.0",

  // Play WS (für Ajax etc.)
  ws
)

// Play-Dev-Settings
Compile / run / fork := true
Compile / run / javaOptions ++= Seq("-Djava.awt.headless=false")

PlayKeys.devSettings ++= Seq(
  "play.server.http.idleTimeout" -> "600 seconds",
  "pekko.http.server.idle-timeout" -> "600s"
)


```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.