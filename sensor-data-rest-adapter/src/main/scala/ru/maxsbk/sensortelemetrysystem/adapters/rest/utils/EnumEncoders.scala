package ru.maxsbk.sensortelemetrysystem.adapters.rest.utils

import io.circe.{ Decoder, Encoder }
import ru.maxsbk.sensortelemetrysystem.models.SensorType

import scala.util.Try

trait EnumEncoders {
  implicit lazy val encodeSourceType: Encoder[SensorType] = Encoder.encodeString.contramap[SensorType](_.toString)
  implicit lazy val decodeSourceType: Decoder[SensorType] = Decoder.decodeString.emapTry { str =>
    Try(SensorType.valueOf(str))
  }
}
