package aia.faulttolerance

import akka.actor.{ActorLogging, PoisonPill, Actor, ActorRef}

class LogProcessActor(dbSupervisor: ActorRef) extends Actor with LogTextParsing with ActorLogging {

  def receive = {
    case LogFile(file) =>
      log.info(s"forwarded from supervisor...${file.getName}")

      val logTexts = parse(file)
      logTexts.foreach {logText: LogText =>
        dbSupervisor ! logText
        log.info(s"sending LogText to db writer supervisor...${logText.text}")
      }

    case _ =>
      log.info("nothing found..")
      self ! PoisonPill

  }
}