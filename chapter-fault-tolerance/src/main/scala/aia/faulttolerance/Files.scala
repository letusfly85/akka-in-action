package aia.faulttolerance

import java.io._

case class NewFile(file: File, timestamp: Long)

case class LogFile(file: File)

/**
 * create table logs (name varchar(40), line int, text varchar(300));
 *
 * @param name
 * @param line
 * @param text
 */
case class LogText(name: String, line: Int, text: String)

/**
 * log fileの中身をすべて操作し改行区切りにしてLogTextのリストを返却
 *
 */
trait LogTextParsing {

  def parse(file: File): List[LogText] = {
    var logTexts: List[LogText] = List()

    try {
      val inputStream: InputStream = new FileInputStream(file)
      val br: BufferedReader =
        new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))

      var index: Int = 0
      Stream.continually(br.readLine()).takeWhile(_ != null).foreach {text: String =>
        logTexts ::= LogText(file.getName, index, text)
        index += 1
      }
    }

    logTexts
  }

}

@SerialVersionUID(1L)
class DiskUnusableError(msg: String)
  extends Error(msg) with Serializable

@SerialVersionUID(1L)
class FileCorruptedException(msg: String, val file: File)
  extends Exception(msg) with Serializable

@SerialVersionUID(1L)
class DbConnectionBrokenException[T](msg: String)
  extends Exception(msg) with Serializable  {
}
