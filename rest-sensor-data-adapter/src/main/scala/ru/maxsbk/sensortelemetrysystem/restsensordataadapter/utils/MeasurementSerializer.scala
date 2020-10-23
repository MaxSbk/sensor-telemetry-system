package ru.maxsbk.sensortelemetrysystem.restsensordataadapter.utils

import com.twitter.bijection.Injection
import com.twitter.bijection.avro.SpecificAvroCodecs
import org.apache.kafka.common.serialization.Serializer
import ru.maxsbk.sensortelemetrysystem.models.Measurement

class MeasurementSerializer extends Serializer[Measurement] {
  override def serialize(topic: String, data: Measurement): Array[Byte] = {
    val recordInjection: Injection[Measurement, Array[Byte]] = SpecificAvroCodecs.toBinary(Measurement.SCHEMA$)
    recordInjection(data)
  }
}
