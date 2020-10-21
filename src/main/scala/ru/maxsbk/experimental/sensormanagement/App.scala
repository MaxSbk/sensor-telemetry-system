package ru.maxsbk.experimental.sensormanagement

import akka.actor.ActorSystem
import akka.stream.alpakka.influxdb.InfluxDbWriteMessage
import akka.stream.alpakka.influxdb.scaladsl.InfluxDbFlow
import akka.stream.scaladsl.{ Sink, Source }
import com.typesafe.config.{ Config, ConfigFactory }
import org.influxdb.dto.Point
import org.influxdb.{ InfluxDB, InfluxDBFactory }

import scala.concurrent.ExecutionContextExecutor
import scala.util.{ Failure, Random, Success }

object App {
  val AppConfig: Config = ConfigFactory.load()
  def apply(): Unit = {
    new App().startApp()
  }
}

class App {
  import App._
  def startApp(): Unit = {
    implicit val system: ActorSystem          = ActorSystem("sensors")
    implicit val ec: ExecutionContextExecutor = system.dispatcher

    implicit val influxDB: InfluxDB = InfluxDBFactory.connect(
      AppConfig.getString("influx_db.url"),
      AppConfig.getString("influx_db.username"),
      AppConfig.getString("influx_db.password")
    )
    val dbName = AppConfig.getString("influx_db.db_name")
    influxDB.setDatabase(dbName)

    Source(Seq("sensor1", "sensor2", "sensor3")).map { _ =>
      val p = Point
        .measurement("Temperature")
        .addField("sensorType", "Simple Type")
        .addField("temp", Random.nextDouble() * 100)
        .build()
      Seq(InfluxDbWriteMessage(p))
    }.via(InfluxDbFlow.create())
      .runWith(Sink.seq)
      .onComplete {
        case Success(x) =>
          println(s"Done, $x")
        case Failure(ex) => println(s"Error, $ex")
      }
  }
}

case class SensorData(sensorName: String, sensorType: String, meas: Double)
