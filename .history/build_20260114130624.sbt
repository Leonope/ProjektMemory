name := """web-tui"""
organization := "com.leo"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.18"

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

libraryDependencies ++= Seq(
  "org.playframework.silhouette" %% "play-silhouette" % "10.0.4",
  //"org.playframework.silhouette" %% "play-silhouette-password-bcrypt" % "10.0.4"
)



