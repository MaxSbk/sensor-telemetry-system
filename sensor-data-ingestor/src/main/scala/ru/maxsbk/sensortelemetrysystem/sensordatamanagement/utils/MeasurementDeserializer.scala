package ru.maxsbk.sensortelemetrysystem.sensordatamanagement.utils

import com.twitter.bijection.Injection
import com.twitter.bijection.avro.SpecificAvroCodecs
import org.apache.kafka.common.serialization.Deserializer
import ru.maxsbk.sensortelemetrysystem.models.Measurement

import scala.util.Try

class MeasurementDeserializer extends Deserializer[Measurement] {
  override def deserialize(topic: String, data: Array[Byte]): Measurement = {
    val recordInjection: Injection[Measurement, Array[Byte]] = SpecificAvroCodecs.toBinary(Measurement.SCHEMA$)
    recordInjection.invert(data)
    Try(recordInjection.invert(data).get).get
  }
}
