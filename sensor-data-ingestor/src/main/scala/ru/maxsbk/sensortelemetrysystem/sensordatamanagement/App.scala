package ru.maxsbk.sensortelemetrysystem.sensordatamanagement

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.kafka.ConsumerMessage.{ CommittableMessage, CommittableOffset, CommittableOffsetBatch }
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

import scala.concurrent.duration.DurationInt
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
  val logger: LoggingAdapter                = system.log
  implicit val influxDB: InfluxDB = InfluxDBFactory
    .connect(
      projectConfig.influxConfig.url,
      projectConfig.influxConfig.username,
      projectConfig.influxConfig.password
    )
    .setDatabase(projectConfig.influxConfig.dbName)

  def startApp(): Unit = {
    val source = Consumer
      .committableSource(
        projectConfig.kafkaConfig.consumerSettings,
        Subscriptions.topics(projectConfig.kafkaConfig.topic)
      )

    val influxWriteMessageFlow = source.mapAsync(projectConfig.kafkaConfig.asyncMapParallelism) { kafkaMessage =>
      influxWrite(kafkaMessage)
    }

    influxWriteMessageFlow
      .groupedWithin(projectConfig.kafkaConfig.groupMaxElems, projectConfig.kafkaConfig.groupTimeWindow)
      .via(InfluxDbFlow.createWithPassThrough)
      .map(x => x.flatMap(f => Some(f.writeMessage.passThrough)))
      .map(group =>
        group.foldLeft(CommittableOffsetBatch.empty) { (batch, elem) =>
          batch.updated(elem)
        }
      )
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
