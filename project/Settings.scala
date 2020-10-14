import sbt.Keys._
import sbt._

object Settings {
  object ScalaVersion {
    val Major = "2.13"
    val It    = s"$Major.3"
  }

  lazy val settings = Seq(
    organization := "ru.maxsbk",
    version := "0.1",
    scalaVersion := ScalaVersion.It,
    scalacOptions ++= CompileOptions.compileOptions,
    scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value,
    libraryDependencies ++= Dependencies.dependencies
  )
}
