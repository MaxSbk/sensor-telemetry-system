import sbt._

lazy val sensorManagement =
  Project(id = "sensor-management", base = file("."))
    .enablePlugins(JavaAppPackaging)
    .settings(Settings.settings,
      mainClass in (Compile) := Some("ru.maxsbk.experimental.sensormanagement.Main"))
