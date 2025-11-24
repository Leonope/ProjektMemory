name := """web-tui"""
organization := "com.leo"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.17"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.2" % Test
libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0"
// WICHTIG: alle benötigten Dependencies für Play + Akka + WebSockets
libraryDependencies ++= Seq(
  guice,
  ws,                                  // Play-WS Client
  "com.typesafe.play" %% "play-streams" % play.core.PlayVersion.current,
  "com.typesafe.akka" %% "akka-actor"   % "2.6.21",
  "com.typesafe.akka" %% "akka-stream"  % "2.6.21"
)

Compile / run / fork := true
Compile / run / javaOptions ++= Seq("-Djava.awt.headless=false")
