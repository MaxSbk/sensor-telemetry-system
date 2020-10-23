package ru.maxsbk.sensortelemetrysystem.restsensordataadapter.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, Behavior, PostStop, PreRestart }
import org.apache.kafka.clients.producer.KafkaProducer
import ru.maxsbk.sensortelemetrysystem.models.Measurement
import ru.maxsbk.sensortelemetrysystem.restsensordataadapter.config.ProjectConfig
import ru.maxsbk.sensortelemetrysystem.restsensordataadapter.utils.ProducerAskResponses.AskResponse
import ru.maxsbk.sensortelemetrysystem.restsensordataadapter.utils.{ ActorLogHelper, KafkaHelper }

import scala.util.Try

object SensorDataProducer extends KafkaHelper with ActorLogHelper {
  sealed trait Command
  final case class ProduceSensorData(message: Measurement, replyTo: ActorRef[AskResponse]) extends Command

  def apply(config: ProjectConfig): Behavior[Command] =
    this(config.kafkaConfig.bootstrapServers, config.kafkaConfig.topic)

  def apply(kafkaServer: String, kafkaTopic: String): Behavior[Command] =
    Behaviors.setup[Command] { _ =>
      val props    = producerProps(kafkaServer)
      val producer = new KafkaProducer[String, Measurement](props)
      produce(producer, kafkaTopic)
    }

  def produce(kafkaProducer: KafkaProducer[String, Measurement], kafkaTopic: String): Behavior[Command] =
    Behaviors
      .receive[Command] {
        case (ctx, ProduceSensorData(message, replyTo)) =>
          ctx.log.debug(
            s"Actor ${fullName(ctx)} got ProduceSensorData with $message for kafka topic $kafkaTopic"
          )
          replyTo ! sendToKafka(kafkaProducer, kafkaTopic, message)(ctx.log)
          Behaviors.same
      }
      .receiveSignal {
        case (ctx, sig @ (PostStop | PreRestart)) =>
          ctx.log.warn(s"Actor ${fullName(ctx)} receive $sig and try to close a kafka producer")
          Try(kafkaProducer.close()).fold(
            ex => ctx.log.warn(s"Failed to close a kafka producer with error $ex(${ex.getMessage})"),
            _ => ctx.log.info("Kafka producer successfully closed")
          )
          Behaviors.empty
      }
      .narrow[Command]

}
