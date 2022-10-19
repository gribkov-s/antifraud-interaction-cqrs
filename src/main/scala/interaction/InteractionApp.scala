package interaction

import akka.NotUsed
import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.{Flow, Sink}
import com.typesafe.config.ConfigFactory
import io.circe.jawn
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer


object InteractionApp {

  def apply(): Behavior[NotUsed] =
    Behaviors.setup { ctx =>

      implicit val system = ctx.system
      val router = ctx.spawn(SubsBlockRouter(), "SubsBlockRouter")

      val config = ConfigFactory.load()
      val consumerConfig = config.getConfig("akka.kafka.consumer")

      val consumerSettings = ConsumerSettings(
        consumerConfig,
        new StringDeserializer,
        new StringDeserializer
      )

      val topic = consumerConfig.getConfig("kafka-clients").getString("common.topic")
      val commonTopicSource = Consumer
        .atMostOnceSource(consumerSettings, Subscriptions.topics(topic))

      val parseMsgFlow = Flow[ConsumerRecord[String, String]].map { msg =>
        val offset = msg.offset()
        val event = jawn.decode[SubsBlockMsg](msg.value())
        event match {
          case Right(msg) =>
            val cmd = ChangeSubsBlock(offset, msg.id, msg.block, 1)
            router ! cmd
            system.log.info(s"SubsBlockConsumer: sended to router: ${cmd.toString}")
          case Left(err) =>  system.log.info(s"Cannot parse offset $offset: $err")
        }
      }

      commonTopicSource.async
        .via(parseMsgFlow).async
        .runWith(Sink.ignore)

      Behaviors.same
    }

  def main(args: Array[String]): Unit = {
    val app = InteractionApp()
    implicit val system: ActorSystem[NotUsed] = ActorSystem(app, "interaction_app")
  }

}
