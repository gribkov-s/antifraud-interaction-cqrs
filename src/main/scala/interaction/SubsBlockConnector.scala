package interaction

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object SubsBlockConnector {

  def apply(id: String, parent: ActorRef[Command]): Behavior[Command] = Behaviors.setup { ctx =>

    implicit val system = ctx.system
    implicit val ec: ExecutionContextExecutor = system.executionContext
    val logger = ctx.log
    val putRequest = SubsBlockClient.putRequest _

    Behaviors.receiveMessage {
      case msg @ ChangeSubsBlock(_, subId, block, attempt) =>

        val requestResult = putRequest(subId, block)

          requestResult.onComplete {
          case Success(true) => {
            parent ! Done(id)
            logger.info(
              s"SubsBlockConnector $id successfully executed command ${msg.toString}"
            )
          }
          case Success(false) => {
            val retryAttempt = attempt + 1
            val retryCmd = msg.copy(attempt = attempt + 1)
            Thread.sleep(10000) ///
            parent ! retryCmd
            logger.info(
              s"SubsBlockConnector $id sent command ${retryCmd.toString} to router for retry attempt $retryAttempt"
            )
          }
          case Failure(err) =>
            logger.info(
              s"SubsBlockConnector $id failed with error: $err"
            )
        }

        Behaviors.stopped
    }
  }

}
