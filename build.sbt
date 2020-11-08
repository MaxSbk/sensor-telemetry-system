import Dependencies.{ Akka, Alpakka, Common, Logging, Testing }
import sbt._

lazy val dataModels = appModule("data-models")
  .enablePlugins(SbtAvrohugger)
  .settings(
    libraryDependencies ++= Seq(Common.Avro),
    sourceGenerators in Compile += (avroScalaGenerateSpecific in Compile).taskValue,
    avroSpecificSourceDirectories in Compile := Seq((sourceDirectory in Compile).value)
  )

lazy val sensorDataManagement = appModule("sensor-data-ingestor")
  .enablePlugins(JavaAppPackaging, DockerPlugin, AshScriptPlugin)
  .settings(
    mainClass in (Compile) := Some("ru.maxsbk.sensortelemetrysystem.sensordatamanagement.Main"),
    libraryDependencies ++= Seq(
      Akka.Actor,
      Akka.Stream,
      Alpakka.Influx,
      Alpakka.Kafka,
      Common.BijectionCore,
      Common.BijectionAvro,
      Logging.Slf4jApi,
      Logging.Log4jSlf4jImpl,
      Testing.ScalaTest,
      Testing.ActorTestKit,
      Testing.AkkaStreamTestkit
    )
  )
  .dependsOn(dataModels)

lazy val restSensorDataAdapter = appModule("sensor-data-rest-adapter")
  .enablePlugins(JavaAppPackaging, DockerPlugin, AshScriptPlugin)
  .settings(
    mainClass in (Compile) := Some("ru.maxsbk.sensortelemetrysystem.restsensordataadapter.Main"),
    libraryDependencies ++= Seq(
      Akka.Actor,
      Akka.Stream,
      Akka.Http,
      Akka.HttpSpray,
      Alpakka.Kafka,
      Common.PureConfig,
      Common.BijectionCore,
      Common.BijectionAvro,
      Common.CirceCore,
      Common.CirceGeneric,
      Common.CirceParser,
      Common.AkkaHttpCircle,
      Logging.Slf4jApi,
      Logging.Log4jSlf4jImpl,
      Testing.ScalaTest,
      Testing.ActorTestKit
    )
  )
  .dependsOn(dataModels)

lazy val mqttSensorDataAdapter = appModule("sensor-data-mqtt-adapter")
  .enablePlugins(JavaAppPackaging, DockerPlugin, AshScriptPlugin)

def appModule(moduleID: String): Project = {
  Project(id = moduleID, base = file(moduleID))
    .settings(name := moduleID, Settings.settings)
}
