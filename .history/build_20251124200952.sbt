name := """web-tui"""
organization := "com.leo"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.17"

// *** Dependencies ***
libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.2" % Test,
  "org.scala-lang.modules" %% "scala-swing"        % "3.0.0",

  // Play WS (vom Play-Plugin, Gruppe ist org.playframework)
  ws,

  // Akka für WebSockets mit ActorFlow
  "com.typesafe.akka"      %% "akka-actor"         % "2.6.21",
  "com.typesafe.akka"      %% "akka-stream"        % "2.6.21"
)

// Play-Dev-Settings
Compile / run / fork := true
Compile / run / javaOptions ++= Seq("-Djava.awt.headless=false")

