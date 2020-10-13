import sbt._

object Version {
  val Akka             = "2.6.10"
  val PureConfig       = "0.14.0"
  val Avro             = "1.9.2"
  val ScalaTest        = "3.1.1"
  val Slf4jApi         = "1.7.30"
  val Log4jSlf4jImpl   = "2.13.0"
}

object Dependencies {

  object Akka {
    lazy val Actor         = "com.typesafe.akka" %% "akka-actor-typed"     % Version.Akka
    lazy val Stream        = "com.typesafe.akka" %% "akka-stream"          % Version.Akka
  }

  object Common {
    val PureConfig     = "com.github.pureconfig"         %% "pureconfig"      % Version.PureConfig
    val Avro           = "org.apache.avro"               % "avro"             % Version.Avro
  }

  object Logging {
    lazy val Slf4jApi       = "org.slf4j"                % "slf4j-api"        % Version.Slf4jApi
    lazy val Log4jSlf4jImpl = "org.apache.logging.log4j" % "log4j-slf4j-impl" % Version.Log4jSlf4jImpl
  }

  object Testing {
    val ScalaTest         = "org.scalatest"           %% "scalatest"                % Version.ScalaTest     % Test
    val ActorTestKit      = "com.typesafe.akka"       %% "akka-actor-testkit-typed" % Version.Akka          % Test
    val AkkaStreamTestkit = "com.typesafe.akka"       %% "akka-stream-testkit"      % Version.Akka          % Test
  }

  val dependencies: Seq[ModuleID] = Seq(
    Akka.Actor,
    Akka.Stream,
    Common.PureConfig,
    Common.Avro,
    Logging.Slf4jApi,
    Logging.Log4jSlf4jImpl,
    Testing.ScalaTest,
    Testing.ActorTestKit,
    Testing.AkkaStreamTestkit
  )

}
