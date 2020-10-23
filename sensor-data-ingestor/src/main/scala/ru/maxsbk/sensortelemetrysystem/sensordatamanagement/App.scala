package ru.maxsbk.sensortelemetrysystem.sensordatamanagement

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.kafka.ConsumerMessage.{ CommittableMessage, CommittableOffset }
import akka.kafka.Subscriptions
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.kafka.scaladsl.{ Committer, Consumer }
import akka.stream.alpakka.influxdb.InfluxDbWriteMessage
import akka.stream.alpakka.influxdb.scaladsl.InfluxDbFlow
import com.typesafe.config.{ Config, ConfigFactory }
import org.influxdb.dto.Point
import org.influxdb.{ InfluxDB, InfluxDBFactory }
import ru.maxsbk.sensortelemetrysystem.models.Measurement
import ru.maxsbk.sensortelemetrysystem.sensordatamanagement.config.ProjectConfig

import scala.concurrent.{ ExecutionContextExecutor, Future }

object App {
  val AppConfig: Config = ConfigFactory.load()
  def apply(): Unit = {
    new App().startApp()
  }
}

class App {
  implicit val system: ActorSystem          = ActorSystem("sensor-data-ingestion-system")
  val projectConfig: ProjectConfig          = ProjectConfig(system.settings.config)
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val influxDB: InfluxDB = InfluxDBFactory
    .connect(
      projectConfig.influxConfig.url,
      projectConfig.influxConfig.username,
      projectConfig.influxConfig.password
    )
    .setDatabase(projectConfig.influxConfig.dbName)

  val logger: LoggingAdapter = system.log

  def startApp(): Unit = {
    val source = Consumer
      .committableSource(
        projectConfig.kafkaConfig.consumerSettings,
        Subscriptions.topics(projectConfig.kafkaConfig.topic)
      )

    val influxWriteMessageFlow = source.mapAsync(10) { kafkaMessage =>
      influxWrite(kafkaMessage)
    }

    val influxResultFlow =
      influxWriteMessageFlow
        .grouped(1)
        .via(InfluxDbFlow.createWithPassThrough)

    val mapperFlow = influxResultFlow
      .map(x => x.headOption.flatMap(f => Some(f.writeMessage.passThrough)))
      .collect {
        case Some(x) => x
      }

    mapperFlow
      .toMat(Committer.sink(projectConfig.kafkaConfig.committerSettings))(DrainingControl.apply)
      .run()

  }

  def influxWrite(
    kafkaMessage: CommittableMessage[String, Measurement]
  )(implicit ec: ExecutionContextExecutor
  ): Future[InfluxDbWriteMessage[Point, CommittableOffset]] = {
    val meas = kafkaMessage.record.value()
    logger.debug(s"New Measurement Value: $meas")
    Future(
      InfluxDbWriteMessage(
        Point
          .measurement(s"${meas.place}_${meas.sensorInfo.sensorType.toString}")
          .addField("sensor-type", meas.sensorInfo.sensorType.toString)
          .addField("sensor-place", meas.place)
          .addField("sensor-unit", meas.unit)
          .addField("sensor-name", meas.sensorInfo.name.getOrElse(""))
          .addField("value", meas.value)
          .build()
      ).withPassThrough(kafkaMessage.committableOffset)
    )
  }
}

case class SensorData(sensorName: String, sensorType: String, meas: Double)
