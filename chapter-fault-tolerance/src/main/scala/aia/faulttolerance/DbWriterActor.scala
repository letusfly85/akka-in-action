package aia.faulttolerance

import java.sql.{PreparedStatement}

import akka.actor.{ActorLogging, PoisonPill, Actor}

case class Reconnect()

class DbWriterActor(conn: DbConnection) extends Actor with ActorLogging {

  def receive = {

    case log: LogText =>
      val sql = s"insert into logs (name, line, text) values ('${log.name}', ${log.line}, '${log.text}')"
      write(sql)

    // 行番号が１のときはエラーを発生させるようにする
      /*
      if (log.line == 1) {
        throw new RuntimeException("something wrong happened!")
      }
      */

    case Reconnect =>
      log.info(s"trying to reconnect to database....")
      conn.reConnect

    case _ =>
      self ! PoisonPill
  }

  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.info("re-connect to database...")

    conn.reConnect()
    super.preRestart(reason, message)
  }

  //TODO DBを落とした際にはconnがnullでわたる事があるので対応を組み込む
  def write(sql: String): Unit = {
    log.info(sql)

    val stmt: PreparedStatement = conn.conn.prepareStatement(sql)
    stmt.execute()
  }
}