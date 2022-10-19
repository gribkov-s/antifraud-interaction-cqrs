import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

package object interaction {


  case class SubsBlockMsg(id: Long, block: Boolean, time: String)

  object SubsBlockMsg {
    val default: SubsBlockMsg = SubsBlockMsg(-1, false, "")
  }

  sealed trait Command
  case class ChangeSubsBlock(offset: Long, subId: Long, block: Boolean, attempt: Int) extends Command
  case class Done(id: String) extends Command

  sealed trait Event
  case class ChangedSubsBlock(id: String, subId: Long, block: Boolean, attempt: Int) extends Event

}
