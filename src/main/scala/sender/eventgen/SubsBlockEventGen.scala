package sender.eventgen

import java.sql.Timestamp
import scala.util.Random

case class SubsBlockEventGen(idLow: Long, idUp: Long) {

  def id: Long = {
    val idRng = idLow to idUp
    val random = new Random
    val elemN = random.nextInt(idRng.length)
    idRng(elemN)
  }

  def time: String = new Timestamp(System.currentTimeMillis()).toString

  def block: Boolean = {
    val random = new Random
    val v = random.nextInt(100)
    if (v % 2 == 1) true else false
  }

}
