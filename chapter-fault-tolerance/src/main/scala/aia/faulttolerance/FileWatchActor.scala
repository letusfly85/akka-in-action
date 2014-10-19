package aia.faulttolerance

import akka.actor.{ActorRef, PoisonPill, Actor}

class FileWatchActor(source: String, logProcSupervisor: ActorRef) extends Actor {

  def receive = {
    case NewFile(file, _) =>
      logProcSupervisor ! LogFile(file)

    case _ =>
      println("nothing given...")
      self ! PoisonPill
  }

}