package aia.faulttolerance

import java.sql.{DriverManager, Connection}

/**
 * create database akkadev;
 * use akkadev;
 * create user akkadev@localhost identified by 'akkadev';
 * grant all privileges on akkadev.* to akkadev@localhost identified by 'akkadev';
 * set password for akkadev@localhost=password('akkadev');
 * flush privileges;
 *
 */
class DbConnection {

  var conn: Connection = _

  def startConnect() = {
    val user = "akkadev"
    val pass = "akkadev"
    val url  = "jdbc:mysql://localhost/akkadev"

    Class.forName("com.mysql.jdbc.Driver")
    conn = DriverManager.getConnection(url,user,pass)
  }

}
