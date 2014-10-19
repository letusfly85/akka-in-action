package aia.faulttolerance

import java.io.File

import akka.actor.{Props, ActorSystem}

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import akka.util.Timeout

object Main extends App {
  val system = ActorSystem("log-writer")

  val dbConn: DbConnection = new DbConnection
  dbConn.startConnect()

  val dbWriterProps: Props = Props(new DbWriterActor(dbConn))

  val dbSuperVisorProps: Props = Props(new DbWriterSupervisorActor(dbWriterProps))

  val logProcSuperVisorProps: Props = Props(new LogProcessSupervisorActor(dbSuperVisorProps))

  val sources = Vector("log/sample.log.1", "log/sample.log.2")
  val topLevelProps = Props(new FileWatchSupervisorActor(sources,logProcSuperVisorProps))

  val log1: LogFile = new LogFile(new File("log/sample.log.1"))
  system.actorOf(logProcSuperVisorProps) ! log1

  system.actorOf(topLevelProps)

  //system.awaitTermination(3.seconds)
  Thread.sleep(3000)
  system.shutdown()


}
