package ru.maxsbk.sensortelemetrysystem.sensordatamanagement.config

import akka.kafka.{ CommitterSettings, ConsumerSettings }
import com.typesafe.config.Config
import org.apache.kafka.common.serialization.StringDeserializer
import ru.maxsbk.sensortelemetrysystem.models.Measurement
import ru.maxsbk.sensortelemetrysystem.sensordatamanagement.utils.MeasurementDeserializer

object ProjectConfig {
  private val ConfigPath: String = "sensor-data-ingestor"

  def apply(rootConfig: Config): ProjectConfig = {
    val conf = rootConfig.getConfig(ConfigPath)
    new ProjectConfig(KafkaConfig(conf), InfluxConfig(conf))
  }

}

case class ProjectConfig(kafkaConfig: KafkaConfig, influxConfig: InfluxConfig)

object KafkaConfig {
  private val ConfigPath               = "kafka-consumer"
  private val CommitterConfigPath      = "kafka-committer"
  private val AsyncMapParallelismCount = "async-parallelism-count"
  private val TopicConfigPath          = "listening-topic"

  def apply(config: Config): KafkaConfig = {
    val thisConfig          = config.getConfig(ConfigPath)
    val consumerSettings    = ConsumerSettings(thisConfig, new StringDeserializer, new MeasurementDeserializer)
    val committerSettings   = CommitterSettings(config.getConfig(CommitterConfigPath))
    val asyncMapParallelism = thisConfig.getInt(AsyncMapParallelismCount)
    val topicName           = thisConfig.getString(TopicConfigPath)
    new KafkaConfig(consumerSettings, committerSettings, asyncMapParallelism, topicName)
  }
}

case class KafkaConfig(
  consumerSettings: ConsumerSettings[String, Measurement],
  committerSettings: CommitterSettings,
  asyncMapParallelism: Int,
  topic: String)

object InfluxConfig {
  private val ConfigPath             = "influx-db"
  private val UrlConfigPath          = "url"
  private val UserNameConfigPath     = "username"
  private val PasswordConfigPath     = "password"
  private val DataBaseNameConfigPath = "db_name"

  def apply(config: Config): InfluxConfig = {
    val thisConfig = config.getConfig(ConfigPath)
    new InfluxConfig(
      thisConfig.getString(UrlConfigPath),
      thisConfig.getString(UserNameConfigPath),
      thisConfig.getString(PasswordConfigPath),
      thisConfig.getString(DataBaseNameConfigPath)
    )
  }
}

case class InfluxConfig(url: String, username: String, password: String, dbName: String)
