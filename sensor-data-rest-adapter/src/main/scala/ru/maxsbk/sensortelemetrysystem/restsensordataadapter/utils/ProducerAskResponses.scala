package ru.maxsbk.sensortelemetrysystem.restsensordataadapter.utils

object ProducerAskResponses {
  sealed trait AskResponse
  final case object ProduceTaskCreated                   extends AskResponse
  final case class ProduceTaskFaulted(faultText: String) extends AskResponse
}
