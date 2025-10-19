name := """web-tui"""
organization := "com.leo"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.17"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.2" % Test
libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0"

Compile / run / fork := true
Compile / run / javaOptions ++= Seq("-Djava.awt.headless=false")
