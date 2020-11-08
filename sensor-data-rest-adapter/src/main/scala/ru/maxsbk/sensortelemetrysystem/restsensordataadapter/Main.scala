package ru.maxsbk.sensortelemetrysystem.restsensordataadapter

import akka.actor.typed.ActorSystem

object Main {
  def main(args: Array[String]): Unit = {
    ActorSystem[Nothing](RestSensorDataAdapter(), "RestSensorDataAdapterSystem")
  }
}
