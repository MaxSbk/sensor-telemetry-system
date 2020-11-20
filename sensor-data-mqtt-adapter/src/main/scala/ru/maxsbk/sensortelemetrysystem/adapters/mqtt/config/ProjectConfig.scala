package ru.maxsbk.sensortelemetrysystem.adapters.mqtt.config

import com.typesafe.config.ConfigFactory
import pureconfig.generic.auto.exportReader
import pureconfig.{ ConfigReader, ConfigSource, Exported }

import scala.util.Right

object ProjectConfig {
  val rootPath                                                   = "sensor-data-mqtt-adapter"
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
  mqttBroker: MqttBroker = MqttBroker.Empty,
  kafkaConfig: KafkaConfig = KafkaConfig.Empty)

object MqttBroker {
  val Empty: MqttBroker = new MqttBroker()
}

case class MqttBroker(url: String = "", clientId: String = "")

object KafkaConfig {
  val Empty: KafkaConfig = new KafkaConfig()
}

case class KafkaConfig(bootstrapServers: String = "", topic: String = "")
