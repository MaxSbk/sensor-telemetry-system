import com.typesafe.sbt.packager.Keys.dockerBaseImage
import sbt.Keys._
import sbt._

object Settings {
  object ScalaVersion {
    val Major = "2.13"
    val It    = s"$Major.3"
  }

  lazy val settings = Seq(
    dockerBaseImage := "openjdk:8-jre-alpine",
    organization := "ru.maxsbk",
    version := "0.1",
    scalaVersion := ScalaVersion.It,
    scalacOptions ++= CompileOptions.compileOptions,
    scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value
    //libraryDependencies ++= Dependencies.CommonDependencies
  )
}
