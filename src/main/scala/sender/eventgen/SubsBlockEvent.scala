package sender.eventgen

import io.circe.Codec
import io.circe.generic.semiauto._


case class SubsBlockEvent(id: Long, block: Boolean, time: String)

object SubsBlockEvent {

  implicit val codec: Codec[SubsBlockEvent] = deriveCodec[SubsBlockEvent]

  def from(idLow: Long, idUp: Long): SubsBlockEvent = {
    val gen = SubsBlockEventGen(idLow: Long, idUp: Long)
    val id = gen.id
    val block = gen.block
    val time = gen.time
    SubsBlockEvent(id, block, time)
  }

}
