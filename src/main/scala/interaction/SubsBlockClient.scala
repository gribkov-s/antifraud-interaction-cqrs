package interaction

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse}
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.typesafe.config.ConfigFactory

import scala.concurrent.{ExecutionContextExecutor, Future}

object SubsBlockClient {

  val config = ConfigFactory.load()
  val consumerApi = config.getConfig("rcvApi")
  val host = consumerApi.getString("host")
  val port = consumerApi.getInt("port")
  val uri = consumerApi.getString("uri")

  def putRequest(subId: Long, blocked: Boolean)
                (implicit sys: ActorSystem[Nothing], ec: ExecutionContextExecutor) = {

    val connectionFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] =
      Http().outgoingConnection(host = host, port = port)

    val serviceUri = s"$uri/$subId/$blocked"
    val request = HttpRequest(uri = serviceUri, method = HttpMethods.PUT)

    val resp =
      Source.single(request)
      .via(connectionFlow)
      .runWith(Sink.head)

    resp.map(r => r.status.isSuccess())
  }

}
