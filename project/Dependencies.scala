import sbt._

object Version {
  val Akka            = "2.6.10"
  val AkkaHttp        = "10.2.1"
  val PureConfig      = "0.14.0"
  val Avro            = "1.9.2"
  val ScalaTest       = "3.1.1"
  val Slf4jApi        = "1.7.30"
  val Log4jSlf4jImpl  = "2.13.0"
  val AlpakkaInfluxDb = "2.0.2"
  val AlpakkaKafka    = "2.0.5"
  val Bijection       = "0.9.7"
  val Circe           = "0.13.0"
  val AkkaHttpCircle  = "1.35.0"
}

object Dependencies {

  object Akka {
    lazy val Actor     = "com.typesafe.akka" %% "akka-actor-typed"     % Version.Akka
    lazy val Stream    = "com.typesafe.akka" %% "akka-stream"          % Version.Akka
    lazy val Http      = "com.typesafe.akka" %% "akka-http"            % Version.AkkaHttp
    lazy val HttpSpray = "com.typesafe.akka" %% "akka-http-spray-json" % Version.AkkaHttp
  }

  object Alpakka {
    lazy val Influx = "com.lightbend.akka" %% "akka-stream-alpakka-influxdb" % Version.AlpakkaInfluxDb
    lazy val Kafka  = "com.typesafe.akka"  %% "akka-stream-kafka"            % Version.AlpakkaKafka
  }

  object Common {
    val PureConfig     = "com.github.pureconfig" %% "pureconfig"      % Version.PureConfig
    val Avro           = "org.apache.avro"       % "avro"             % Version.Avro
    val CirceCore      = "io.circe"              %% "circe-core"      % Version.Circe
    val CirceGeneric   = "io.circe"              %% "circe-generic"   % Version.Circe
    val CirceParser    = "io.circe"              %% "circe-parser"    % Version.Circe
    val BijectionCore  = "com.twitter"           %% "bijection-core"  % Version.Bijection
    val BijectionAvro  = "com.twitter"           %% "bijection-avro"  % Version.Bijection
    val AkkaHttpCircle = "de.heikoseeberger"     %% "akka-http-circe" % Version.AkkaHttpCircle
  }

  object Logging {
    lazy val Slf4jApi       = "org.slf4j"                % "slf4j-api"        % Version.Slf4jApi
    lazy val Log4jSlf4jImpl = "org.apache.logging.log4j" % "log4j-slf4j-impl" % Version.Log4jSlf4jImpl
  }

  object Testing {
    val ScalaTest         = "org.scalatest"     %% "scalatest"                % Version.ScalaTest % Test
    val ActorTestKit      = "com.typesafe.akka" %% "akka-actor-testkit-typed" % Version.Akka      % Test
    val AkkaStreamTestkit = "com.typesafe.akka" %% "akka-stream-testkit"      % Version.Akka      % Test
  }

}
