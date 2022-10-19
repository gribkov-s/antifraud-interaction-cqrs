package receiver

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import receiver.dao.entities.SubsBlockRecord


package object dto {

  case class SubsBlockRecordDTO(id: Long, blocked: Boolean)

  object SubsBlockRecordDTO {

    implicit val codec: Codec[SubsBlockRecordDTO] = deriveCodec[SubsBlockRecordDTO]

    def from(subsBlockRecord: SubsBlockRecord): SubsBlockRecordDTO = SubsBlockRecordDTO(
      subsBlockRecord.id,
      subsBlockRecord.blocked
    )
  }

}
