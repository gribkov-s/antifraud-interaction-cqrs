package interaction

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.{Flow, Sink}
import com.typesafe.config.ConfigFactory
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import io.circe.jawn
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.{ExecutionContextExecutor, Future}
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}

import scala.util.{Failure, Success}


object InteractionStreamApp extends App {

  implicit val system: ActorSystem = ActorSystem("consumer-sys")
  implicit val ec: ExecutionContextExecutor = system.dispatcher


  def requestSubsBlock(event: SubsBlockMsg) = {
    val serviceUri = "http://localhost:8080/receiver/subsblocking"
    val uri = s"$serviceUri/${event.id}/${event.block}"
    val request = HttpRequest(method = HttpMethods.PUT, uri = uri)
    val resp = Http().singleRequest(request)
    resp.map(r => r.status.isSuccess())
  }

  val config = ConfigFactory.load()
  val consumerConfig = config.getConfig("akka.kafka.consumer")

  val consumerSettings = ConsumerSettings(
    consumerConfig,
    new StringDeserializer,
    new StringDeserializer
  )

  val commonTopicSource = Consumer
    .atMostOnceSource(consumerSettings, Subscriptions.topics("common_subs_block"))

  val parserFlow = Flow[ConsumerRecord[String, String]].map { msg =>
    println(msg.offset())
    val event = jawn.decode[SubsBlockMsg](msg.value())
    event match {
      case Right(value) => value
      case Left(_) => SubsBlockMsg.default
    }
  }

  val printMessage = Flow[SubsBlockMsg].map { event =>
    println(event)
    event
  }

  val sendRequestFlow = Flow[SubsBlockMsg].map { event =>
    requestSubsBlock(event)
  }

  val printResponseStatusFlow = Flow[Future[Boolean]].map { r =>
    r.onComplete{
      case Success(value) => println(value)
      case Failure(exception) => println(exception)
    }
  }

  commonTopicSource.async
    .via(parserFlow).async
    .via(sendRequestFlow).async
    .via(printResponseStatusFlow).async
    .runWith(Sink.ignore)


}
