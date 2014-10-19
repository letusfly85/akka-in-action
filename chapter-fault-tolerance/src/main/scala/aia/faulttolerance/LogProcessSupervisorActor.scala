package aia.faulttolerance

import akka.actor.SupervisorStrategy.Resume
import akka.actor._

class LogProcessSupervisorActor(dbWriterSupervisor: Props) extends Actor with ActorLogging {

  override def supervisorStrategy = OneForOneStrategy() {
    case _: DiskUnusableError =>
      log.info("Resume!")
      Resume
  }

  val dbSupervisor = context.actorOf(dbWriterSupervisor)
  val logProcProps = Props(new LogProcessActor(dbSupervisor))

  val logProcessor = context.actorOf(logProcProps)

  def receive = {
    case logFile: LogFile =>
      log.info(s"getting log files.. ${logFile.file.getName}")
      logProcessor forward logFile

    case _ =>
      log.info("nothing given..")
      self ! PoisonPill
  }

}
