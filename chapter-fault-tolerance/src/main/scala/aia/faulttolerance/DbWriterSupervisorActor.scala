package aia.faulttolerance

import java.sql.SQLException
import java.util.concurrent.TimeUnit

import akka.pattern.ask
import akka.actor.SupervisorStrategy.{Escalate, Stop, Restart}
import akka.actor.{ActorLogging, OneForOneStrategy, Actor, Props}
import akka.util.Timeout
import scala.concurrent.duration._

class DbWriterSupervisorActor(writerProps: Props) extends Actor with ActorLogging {

  val writer = context.actorOf(writerProps)
  implicit val timeout = Timeout(10000, TimeUnit.MILLISECONDS)

  override def supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 30, withinTimeRange = 2 seconds) {

    case e: SQLException =>
      log.error(e.getCause, e.getMessage)
      log.info(s"${e.getErrorCode.toString}: check your database state, stop actor system.")
      writer ? Reconnect
      Thread.sleep(10000)
      Restart

    case e: RuntimeException =>
      log.error(e.getCause, e.getMessage)
      log.info("re-start db writer actor system!")
      Stop

    case e: Exception =>
      log.info(s"escalate error")
      Escalate
  }

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
