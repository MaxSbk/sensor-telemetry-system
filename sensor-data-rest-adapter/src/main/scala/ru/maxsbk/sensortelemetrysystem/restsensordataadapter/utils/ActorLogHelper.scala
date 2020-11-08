package ru.maxsbk.sensortelemetrysystem.restsensordataadapter.utils

import akka.actor.typed.scaladsl.ActorContext

trait ActorLogHelper {
  protected def fullName(context: ActorContext[_]) = s"${context.self.path.parent.name}/${context.self.path.name}"
}
