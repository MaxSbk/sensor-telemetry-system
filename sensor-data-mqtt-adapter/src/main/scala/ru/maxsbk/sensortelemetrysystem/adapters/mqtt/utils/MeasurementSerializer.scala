package ru.maxsbk.sensortelemetrysystem.adapters.mqtt.utils

import com.twitter.bijection.Injection
import com.twitter.bijection.avro.SpecificAvroCodecs
import org.apache.kafka.common.serialization.Serializer
import ru.maxsbk.sensortelemetrysystem.models.Measurement

import scala.util.Try

class MeasurementSerializer extends Serializer[Measurement] {
  override def serialize(topic: String, data: Measurement): Array[Byte] = {
    val recordInjection: Injection[Measurement, Array[Byte]] = SpecificAvroCodecs.toBinary(Measurement.SCHEMA$)
    Try(recordInjection.apply(data)).get
  }
}
