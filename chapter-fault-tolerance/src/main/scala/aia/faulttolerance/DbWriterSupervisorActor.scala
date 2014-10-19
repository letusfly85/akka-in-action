package aia.faulttolerance

import java.sql.SQLException

import akka.actor.SupervisorStrategy.{Escalate, Stop, Restart}
import akka.actor.{ActorLogging, OneForOneStrategy, Actor, Props}
import scala.concurrent.duration._

class DbWriterSupervisorActor(writerProps: Props) extends Actor with ActorLogging {

  //override def supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
  override def supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 2 seconds) {

    case e: SQLException =>
      log.error(e.getCause, e.getMessage)
      log.info(s"${e.getErrorCode.toString}: check your database state, stop actor system.")
      Stop

    case e: RuntimeException =>
      log.error(e.getCause, e.getMessage)
      log.info("re-start db writer actor system!")
      Stop

    case e: Exception =>
      log.info(s"escalate error")
      Escalate
  }

  val writer = context.actorOf(writerProps)

  def receive = {
    case LogText(name, line, text) =>
      log.info(s"getting from supervisor...${name}..${line}..${text}")
      writer forward LogText(name, line, text)

    case _ =>
      log.info("something happened.")
  }

  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.info("re-connect session")

    writer ! Reconnect
    super.preRestart(reason, message)
  }
}
