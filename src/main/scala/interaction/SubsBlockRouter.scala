package interaction

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object SubsBlockRouter {

  def apply(): Behavior[Command] = Behaviors.setup { ctx =>
    Behaviors.receiveMessage {
      case msg @ ChangeSubsBlock(offset, _, _, attempt) =>
        val connectorId = s"$offset-$attempt"
        val connector =
          ctx.spawn(SubsBlockConnector(connectorId, ctx.self), s"SubsBlockConnector-$connectorId")
        connector ! msg
        ctx.log.info(
          s"SubsBlockRouter created SubsBlockConnector $connectorId and sent to it command ${msg.toString}"
        )
        Behaviors.same
      case Done(id) =>
        ctx.log.info(s"SubsBlockRouter received Done message from connector $id")
        Behaviors.same
    }
  }

}
