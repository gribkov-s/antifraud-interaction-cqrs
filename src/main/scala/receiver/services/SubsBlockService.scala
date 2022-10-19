package receiver.services

import zio.Has
import receiver.dto._
import zio.ZLayer
import zio.ZIO
import zio.RIO
import zio.macros.accessible
import java.sql.Timestamp
import receiver.dao.repositories.SubsBlockRepository
import receiver.db.DataSource
import receiver.db
import receiver.dao.entities.SubsBlockRecord


@accessible
object SubsBlockService {

  type SubsBlockService = Has[Service]

  trait Service{
    def find(subscriber: Long): ZIO[DataSource, Option[Throwable], SubsBlockRecordDTO]
    def insert(subsBlockRecord: SubsBlockRecordDTO): RIO[DataSource, Unit]
    def update(subsBlockRecord: SubsBlockRecordDTO): RIO[DataSource, Unit]
    def delete(id: Long): RIO[DataSource, Unit]
    def upsert(subsBlockRecord: SubsBlockRecordDTO): RIO[DataSource, Unit]
  }

  class Impl(subsBlockRepository: SubsBlockRepository.Service) extends Service {

    val ctx = db.Ctx
    import ctx._

    def find(subscriber: Long): ZIO[DataSource, Option[Throwable], SubsBlockRecordDTO] = for {
      result <- subsBlockRepository.find(subscriber).some
    } yield SubsBlockRecordDTO.from(result)

    def insert(subsBlockRecord: SubsBlockRecordDTO): RIO[DataSource, Unit] = for {
        timestamp <- ZIO.succeed(new Timestamp(System.currentTimeMillis()))
        subsBlock = SubsBlockRecord(subsBlockRecord.id, subsBlockRecord.blocked, timestamp)
        _ <- subsBlockRepository.insert(subsBlock)
      } yield ()

    def update(subsBlockRecord: SubsBlockRecordDTO): RIO[DataSource, Unit] = for {
      timestamp <- ZIO.succeed(new Timestamp(System.currentTimeMillis()))
      subsBlock = SubsBlockRecord(subsBlockRecord.id, subsBlockRecord.blocked, timestamp)
      _ <- subsBlockRepository.update(subsBlock)
    } yield ()

    def delete(id: Long): RIO[DataSource, Unit] = for {
      _ <- subsBlockRepository.delete(id)
    } yield ()

    def upsert(subsBlockRecord: SubsBlockRecordDTO): RIO[DataSource, Unit] = for {
      timestamp <- ZIO.succeed(new Timestamp(System.currentTimeMillis()))
      subsBlock = SubsBlockRecord(subsBlockRecord.id, subsBlockRecord.blocked, timestamp)
      cnt <- subsBlockRepository.count(subsBlockRecord.id)
      _ <- if (cnt == 0) subsBlockRepository.insert(subsBlock) else subsBlockRepository.update(subsBlock)
    } yield ()
  }

  val live: ZLayer[SubsBlockRepository.SubsBlockRepository, Nothing, SubsBlockService.SubsBlockService] =
    ZLayer.fromService[SubsBlockRepository.Service, SubsBlockService.Service](subsBlockRepo =>
      new Impl(subsBlockRepo)
    )

}
