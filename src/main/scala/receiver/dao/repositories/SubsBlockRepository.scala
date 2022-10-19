package receiver.dao.repositories

import zio.{Has, ULayer, ZIO, ZLayer}
import io.getquill.{EntityQuery, Quoted}
import io.getquill.context.ZioJdbc._
import receiver.db
import receiver.dao.entities.SubsBlockRecord


object SubsBlockRepository {

  type SubsBlockRepository = Has[Service]

  import db.Ctx._

  trait Service{
    def find(id: Long): QIO[Option[SubsBlockRecord]]
    def count(id: Long): QIO[Long]
    def insert(phoneRecord: SubsBlockRecord): QIO[Unit]
    def update(phoneRecord: SubsBlockRecord): QIO[Unit]
    def delete(id: Long): QIO[Unit]
  }

  class ServiceImpl extends Service {

    val subsBlockSchema: Quoted[EntityQuery[SubsBlockRecord]] = quote {
      querySchema[SubsBlockRecord]("public.subscriber_blocking")
    }

    def find(id: Long): QIO[Option[SubsBlockRecord]] =
      run(subsBlockSchema.filter(_.id == lift(id)).take(1)).map(_.headOption)

    def count(id: Long): QIO[Long] = run(subsBlockSchema.filter(_.id == lift(id)).size)

    def insert(subscriber: SubsBlockRecord): QIO[Unit] = run(subsBlockSchema.insert(lift(subscriber))).map(_ => ())

    def update(subscriber: SubsBlockRecord): QIO[Unit] = run(
      subsBlockSchema.filter(_.id == lift(subscriber.id)).update(lift(subscriber))
    ).map(_ => ())

    def delete(id: Long): QIO[Unit] = run(subsBlockSchema.filter(_.id == lift(id)).delete).map(_ => ())

  }

  val live: ULayer[SubsBlockRepository] = ZLayer.succeed(new ServiceImpl)

}
