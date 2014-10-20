package aia.faulttolerance

import java.net.ConnectException
import java.sql.SQLException

import akka.actor.SupervisorStrategy.{Escalate, Stop, Restart}
import akka.actor._
import scala.concurrent.duration._

class DbWriterSupervisorActor(writerProps: Props) extends Actor with ActorLogging {

  val writer = context.actorOf(writerProps)

  override def supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 30, withinTimeRange = 5 seconds) {

    case e: SQLException =>
      log.info(s"check your database state......")
      Restart

    //TODO DBWriterでのハンドリングをうまくやれば発生しないか確認・対応
    case e: ConnectException =>
      Restart

    //TODO DBWriterでのハンドリングをうまくやれば発生しないか確認・対応
    case e: NullPointerException =>
      Restart

    case e: RuntimeException =>
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
}
