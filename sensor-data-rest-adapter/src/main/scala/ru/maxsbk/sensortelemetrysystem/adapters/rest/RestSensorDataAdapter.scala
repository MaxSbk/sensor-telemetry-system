package ru.maxsbk.sensortelemetrysystem.adapters.rest

import akka.actor.typed.{ActorRef, Behavior, SupervisorStrategy}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, Routers}
import ru.maxsbk.sensortelemetrysystem.adapters.rest.actors.SensorDataProducer
import ru.maxsbk.sensortelemetrysystem.adapters.rest.config.ProjectConfig
import ru.maxsbk.sensortelemetrysystem.adapters.rest.routes.MainRoutes
import ru.maxsbk.sensortelemetrysystem.adapters.rest.utils.HttpServerHelper

object RestSensorDataAdapter extends HttpServerHelper {
  private val systemConfig = ProjectConfig()
  override def port: Int   = systemConfig.httpEndpoint.port

  def apply(): Behavior[Nothing] = {
    Behaviors.setup[Nothing] { implicit context =>
      val avroMessageProducerPool =
        generatePool(SensorDataProducer(systemConfig), "SensorDataProducer", systemConfig.producer.poolSize)

      val mainRoutes = new MainRoutes(systemConfig, avroMessageProducerPool)(context.system)
      startHttpServer(mainRoutes.route)(context.system)
      Behaviors.empty
    }
  }

  private def generatePool[A](
    actorBehavior: Behavior[A],
    name: String,
    poolSize: Int
  )(implicit context: ActorContext[Nothing]
  ): ActorRef[A] = {
    val pool = Routers
      .pool(poolSize = poolSize)(
        Behaviors.supervise(actorBehavior).onFailure[Throwable](SupervisorStrategy.restart)
      )
      .withRoundRobinRouting()
    context.spawn(pool, name)
  }
}
