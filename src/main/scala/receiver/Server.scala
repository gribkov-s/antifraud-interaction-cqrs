package receiver

import cats.effect.{ExitCode => CatsExitCode}
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import zio.blocking.Blocking
import zio.clock.Clock
import zio.interop.catz._
import zio.{RIO, ZIO}
import configuration._
import org.http4s.HttpApp
import receiver.api.SubsBlockAPI
import receiver.services.SubsBlockService
import receiver.db._
import receiver.dao.repositories.SubsBlockRepository
import org.http4s.implicits._


object Server {

  type AppEnvironment = SubsBlockService.SubsBlockService with SubsBlockRepository.SubsBlockRepository with Configuration with
    Clock with Blocking with LiquibaseService.Liqui with LiquibaseService.LiquibaseService with DataSource

  val appEnvironment = Configuration.live >+> Blocking.live >+> zioDS >+> LiquibaseService.liquibaseLayer ++
    SubsBlockRepository.live >+> SubsBlockService.live ++ LiquibaseService.live

  type AppTask[A] = RIO[AppEnvironment, A]

  private def httApp(uri: String): HttpApp[AppTask] =
    Router[AppTask](uri -> new SubsBlockAPI().route).orNotFound

  val server = for {
    config <- zio.config.getConfig[RcvConfig]
    _ <- LiquibaseService.performMigration
    server <- ZIO.runtime[AppEnvironment].flatMap{ implicit rts =>
      val ec = rts.platform.executor.asEC
      BlazeServerBuilder[AppTask](ec)
        .bindHttp(config.receiver.api.port, config.receiver.api.host)
        .withHttpApp(httApp(config.receiver.api.uri))
        .serve
        .compile[AppTask, AppTask, CatsExitCode]
        .drain
    }

  } yield server
}
