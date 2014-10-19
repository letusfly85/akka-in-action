package aia.faulttolerance

import akka.actor.{ActorSystem, Props}

object Main extends App {

  val dbConn: DbConnection = new DbConnection
  dbConn.startConnect()

  val dbWriterActor = ActorSystem("dbWriterActor").actorOf(Props(new DbWriterActor(dbConn)))

  val john: User = User("John", 35, 1111112)

  dbWriterActor ! john

  dbWriterActor ! "done!"
}
