package ru.maxsbk.sensortelemetrysystem.adapters.mqtt

import akka.Done
import akka.actor.ActorSystem
import akka.stream.alpakka.mqtt.{MqttConnectionSettings, MqttQoS, MqttSubscriptions}
import akka.stream.alpakka.mqtt.scaladsl.{MqttMessageWithAck, MqttSource}
import akka.stream.scaladsl.{Sink, Source}
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import ru.maxsbk.sensortelemetrysystem.adapters.mqtt.config.ProjectConfig

import scala.concurrent.{ExecutionContextExecutor, Future}

object Main {
  private val systemConfig                  = ProjectConfig()
  implicit val system: ActorSystem          = ActorSystem("sensor-data-ingestion-system")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  def main(args: Array[String]) = {
    val connectionSettings = MqttConnectionSettings(
      systemConfig.mqttBroker.url,
      "test-scala-client",
      new MemoryPersistence
    )

    val testingTopic = "topic"

    val mqttSource: Source[MqttMessageWithAck, Future[Done]] =
      MqttSource.atLeastOnce(
        connectionSettings
          .withClientId(clientId = "source-spec/source1")
          .withCleanSession(false),
        MqttSubscriptions(testingTopic, MqttQoS.AtLeastOnce),
        bufferSize = 8
      )

    mqttSource
      .mapAsync(1)(messageWithAck => messageWithAck.ack().map(_ => messageWithAck.message))
      .runWith(Sink.seq)
  }
}
