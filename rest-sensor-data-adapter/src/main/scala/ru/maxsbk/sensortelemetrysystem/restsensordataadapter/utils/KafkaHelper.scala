package ru.maxsbk.sensortelemetrysystem.restsensordataadapter.utils

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.slf4j.Logger
import ru.maxsbk.sensortelemetrysystem.models.Measurement
import ru.maxsbk.sensortelemetrysystem.restsensordataadapter.utils.ProducerAskResponses.{AskResponse, ProduceTaskCreated, ProduceTaskFaulted}

import scala.util.Try

trait KafkaHelper {
  protected def producerProps(kafkaServer: String): Properties = {
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    props.put(
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
      "ru.maxsbk.sensortelemetrysystem.restsensordataadapter.utils.MeasurementSerializer"
    )
    props.put(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, "0")
    props.put(ProducerConfig.ACKS_CONFIG, "all")
    props.put(ProducerConfig.RETRIES_CONFIG, "0")
    props
  }

  protected def producerRecord(topic: String, key: String, value: Measurement): ProducerRecord[String, Measurement] = {
    new ProducerRecord[String, Measurement](topic, key, value)
  }

  protected def sendToKafka(
    producer: KafkaProducer[String, Measurement],
    topic: String,
    message: Measurement
  )(
    log: Logger
  ): AskResponse = {
    Try {
      val record = producerRecord(topic, message.sensorInfo.id, message)
      producer.send(record)
    }.fold(ex => {
      log.error(s"Failed to produce record to the topic $topic", ex)
      ProduceTaskFaulted(ex.getMessage)
    }, _ => ProduceTaskCreated)
  }
}
