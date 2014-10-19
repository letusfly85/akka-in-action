package aia.faulttolerance

import akka.actor.{PoisonPill, Actor}

class DbWriterActor(conn: DbConnection) extends Actor {

  def receive = {
    case msg: String =>
      println(msg)
      sender() ! PoisonPill
      sys.exit(0)

    case user: User =>
      user.sayMyAge()

    case _ =>
      println(getClass.getName)
      throw new Exception("default error!")
  }

  override def preRestart(cause: Throwable, msg: Option[Any]) = {
    println("pre start!")
  }

}
