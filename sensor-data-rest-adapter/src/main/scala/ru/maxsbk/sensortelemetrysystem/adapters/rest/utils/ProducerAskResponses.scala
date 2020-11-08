package ru.maxsbk.sensortelemetrysystem.adapters.rest.utils

object ProducerAskResponses {
  sealed trait AskResponse
  final case object ProduceTaskCreated                   extends AskResponse
  final case class ProduceTaskFaulted(faultText: String) extends AskResponse
}
