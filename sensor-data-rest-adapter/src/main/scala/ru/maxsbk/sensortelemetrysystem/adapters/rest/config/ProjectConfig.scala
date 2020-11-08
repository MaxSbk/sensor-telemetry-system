package ru.maxsbk.sensortelemetrysystem.adapters.rest.config

import com.typesafe.config.ConfigFactory
import pureconfig.{ ConfigReader, ConfigSource, Exported }
import pureconfig.generic.auto.exportReader
import scala.util.Right

object ProjectConfig {
  val rootPath                                                   = "initial-state-processor"
  implicit val derivation: Exported[ConfigReader[ProjectConfig]] = exportReader[ProjectConfig]
  def apply(): ProjectConfig = {
    val rootConfig = ConfigFactory.load().getConfig(rootPath)
    ConfigSource.fromConfig(rootConfig).load[ProjectConfig] match {
      case Right(b) => b
      case _ =>
        throw new IllegalArgumentException(
          "Invalid configuration found. Please check application.conf and ProjectConfig.scala"
        )
    }
  }
}

case class ProjectConfig(
  producer: Producer = Producer.Empty,
  routes: Routes = Routes.Empty,
  httpEndpoint: HttpEndpoint = HttpEndpoint.Empty,
  kafkaConfig: KafkaConfig = KafkaConfig.Empty)

object Producer {
  val Empty: Producer = new Producer()
}

case class Producer(poolSize: Int = 0)

object Routes {
  val Empty: Routes = new Routes()
}

case class Routes(askTimeout: Long = 0)

object HttpEndpoint {
  val Empty: HttpEndpoint = new HttpEndpoint()
}

case class HttpEndpoint(port: Int = 0)

object KafkaConfig {
  val Empty: KafkaConfig = new KafkaConfig()
}

case class KafkaConfig(bootstrapServers: String = "", topic: String = "")
