name := """web-tui"""
organization := "com.leo"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.17"

// Dependencies
libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.2" % Test,
  "org.scala-lang.modules" %% "scala-swing"        % "3.0.0",
  ws,
  "com.google.firebase" % "firebase-admin" % "9.4.0",
  )


// Play-Dev-Settings
Compile / run / fork := true
Compile / run / javaOptions ++= Seq("-Djava.awt.headless=false")

PlayKeys.devSettings ++= Seq(
  "play.server.http.idleTimeout" -> "600 seconds",
  "pekko.http.server.idle-timeout" -> "600s"
)



