package ru.maxsbk.experimental.sensormanagement

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.influxdb.InfluxDbWriteMessage
import akka.stream.alpakka.influxdb.scaladsl.InfluxDbFlow
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.ConfigFactory
import org.influxdb.{InfluxDB, InfluxDBFactory}
import org.influxdb.dto.Point

import scala.util.{Failure, Random, Success}

object App {
  val AppConfig = ConfigFactory.load()
  def apply() = {
    new App().startApp()
  }
}

class App {
  import App._
  def startApp() = {
    implicit val system = ActorSystem("sensors")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    implicit val influxDB: InfluxDB = InfluxDBFactory.connect(
      AppConfig.getString("influx_db.url"),
      AppConfig.getString("influx_db.username"),
      AppConfig.getString("influx_db.password")
    )
    val dbName = AppConfig.getString("influx_db.db_name")
    influxDB.setDatabase(dbName)

    Source(Seq("sensor1", "sensor2", "sensor3")).map{sensorName =>
      val p = Point.measurement("Temperature")
        .addField("sensorType", "Simple Type")
        .addField("temp", Random.nextDouble() * 100).build()
      Seq(InfluxDbWriteMessage(p))
    }
      .via(InfluxDbFlow.create())
      .runWith(Sink.seq)
      .onComplete {
        case Success(x) =>
          println(s"Done, $x")
        case Failure(ex) => println(s"Error, $ex")
      }
  }
}

case class SensorData(sensorName: String, sensorType:String, meas: Double)