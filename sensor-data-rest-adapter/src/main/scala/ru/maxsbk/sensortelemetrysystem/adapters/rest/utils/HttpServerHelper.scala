package ru.maxsbk.sensortelemetrysystem.adapters.rest.utils

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route

import scala.concurrent.ExecutionContextExecutor
import scala.util.{ Failure, Success }

trait HttpServerHelper {

  def port: Int

  protected def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val futureBinding = Http().newServerAt("0.0.0.0", port).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server starts at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint", ex)
        system.terminate()
    }
  }
}
