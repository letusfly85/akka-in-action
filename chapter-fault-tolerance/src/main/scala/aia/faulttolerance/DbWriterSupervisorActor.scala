package aia.faulttolerance

import akka.actor.SupervisorStrategy.{Stop, Restart}
import akka.actor.{ActorLogging, OneForOneStrategy, Actor, Props}

class DbWriterSupervisorActor(writerProps: Props) extends Actor with ActorLogging {

  override def supervisorStrategy = OneForOneStrategy() {
    case e: Exception =>
      log.error(e.getCause, e.getMessage)
      log.info("re-start db writer actor system!")
      Restart

    case _ =>
      Stop
  }

  val writer = context.actorOf(writerProps)

  def receive = {
    case LogText(name, line, text) =>
      log.info(s"getting from supervisor...${name}..${line}..${text}")
      writer forward LogText(name, line, text)

    case _ =>
      log.info("something happened.")
  }

}
