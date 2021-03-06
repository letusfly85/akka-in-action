package aia.faulttolerance

import java.io.File

import akka.actor.{Props, ActorSystem}
import scala.concurrent.duration._

/**
 * refs: http://doc.akka.io/docs/akka/snapshot/scala/fault-tolerance.html#fault-tolerance-scala
 *
 *
 */
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

  // １度だけ実行させる
  //system.actorOf(logProcSuperVisorProps) ! log1
  //system.actorOf(topLevelProps)
  //val system = context.system

  // 3秒経過したのちに、その後は５秒間隔でアクターを実行させ続ける
  implicit val executionContext = system.dispatcher
  system.scheduler.schedule(3 seconds, 5 seconds, system.actorOf(logProcSuperVisorProps), log1)

  //system.awaitTermination(30.seconds)
  //Thread.sleep(3000)
  //system.shutdown()
}
