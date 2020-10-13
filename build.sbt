import sbt._

lazy val microservice =
  Project(id = "sensor-management", base = file("."))
    .enablePlugins(JavaAppPackaging)
    .settings(Settings.settings)
