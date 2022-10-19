package receiver.api

import zio.{RIO, ZIO}
import io.circe.{Decoder, Encoder, jawn}
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import org.http4s.EntityEncoder
import org.http4s.EntityDecoder
import org.http4s.circe._
import zio.interop.catz._
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes
import receiver.dto._
import receiver.services.SubsBlockService
import receiver.db.DataSource


class SubsBlockAPI[R <: SubsBlockService.SubsBlockService with DataSource]  {

  type SubsBlockTask[A] =  RIO[R, A]

  val dsl = Http4sDsl[SubsBlockTask]
  import dsl._


  implicit def jsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[SubsBlockTask, A] =
    jsonOf[SubsBlockTask, A]

  implicit def jsonEncoder[A](implicit decoder: Encoder[A]): EntityEncoder[SubsBlockTask, A] =
    jsonEncoderOf[SubsBlockTask, A]


  def route: HttpRoutes[SubsBlockTask] = HttpRoutes.of[SubsBlockTask]{

    case GET -> Root / id => SubsBlockService.find(id.toLong).foldM(
      err => NotFound(),
      result => Ok(result.blocked)
    )

    case POST -> Root / id => (
      for {
        record <- ZIO.succeed(SubsBlockRecordDTO(id.toLong, false))
        result <- SubsBlockService.upsert(record)
      } yield result
      ).foldM(
      err => BadRequest(err.getMessage()),
      result => Ok(result)
    )

    case PUT -> Root / id / blocked => (
      for {
        record <- ZIO.succeed(SubsBlockRecordDTO(id.toLong, blocked.toBoolean))
        result <- SubsBlockService.upsert(record)
      } yield result
    ).foldM(
      err => BadRequest(err.getMessage()),
      result => Ok(result)
    )

    case DELETE -> Root / id => SubsBlockService.delete(id.toLong).foldM(
      err => BadRequest(s"$id not found"),
      result => Ok(result)
    )
  }

}
