package aia.faulttolerance

import akka.actor.SupervisorStrategy.Stop
import akka.actor._

class FileWatchSupervisorActor(sources: Vector[String], logProcSuperProps: Props) extends Actor with ActorLogging {

  var fileWatchers: Vector[ActorRef] = sources.map { source: String =>
    log.info(source)

    val logProcSupervisor = context.actorOf(logProcSuperProps)
    val fileWatcher = context.actorOf(Props(new FileWatchActor(source, logProcSupervisor)))
    context.watch(fileWatcher)
    fileWatcher

  }

  override def supervisorStrategy = AllForOneStrategy() {
    case _: DiskUnusableError
        => Stop
  }

  def receive = {
    case Terminated(fileWatcher) =>
      fileWatchers = fileWatchers.filterNot(w => w == fileWatcher)
      if (fileWatchers.isEmpty) self ! PoisonPill
  }
}