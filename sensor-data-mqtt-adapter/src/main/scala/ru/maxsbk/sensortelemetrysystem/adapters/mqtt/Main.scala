package ru.maxsbk.sensortelemetrysystem.adapters.mqtt

object Main {
  def main(args: Array[String]) = {
    val connectionSettings = MqttConnectionSettings(
      "tcp://localhost:1883", // (1)
      "test-scala-client", // (2)
      new MemoryPersistence // (3)
    )
  }
}
