package aia.faulttolerance

import java.sql.{SQLException, PreparedStatement}

import akka.actor.{ActorLogging, PoisonPill, Actor}

class DbWriterActor(conn: DbConnection) extends Actor with ActorLogging {

  def receive = {
    case log: LogText =>

      // 行番号が１のときはエラーを発生させるようにする
      if (log.line == 1) {
        throw new Exception("something happened!!")
      }
      val sql = s"insert into logs (name, line, text) values ('${log.name}', ${log.line}, '${log.text}')"
      write(sql)

    case msg: String =>
      log.info(s"exit with message: ${msg}")
      sys.exit(0)

    case _ =>
      self ! PoisonPill
      throw new DbConnectionBrokenException[SQLException]("error!")
  }

  /*
  override def preRestart(cause: Throwable, msg: Option[Any]) = {
    println("pre start!")
  }
  */

  def write(sql: String): Unit = {
    log.info(sql)

    val stmt: PreparedStatement = conn.conn.prepareStatement(sql)
    stmt.execute()
  }

}
