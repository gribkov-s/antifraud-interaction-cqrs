package receiver

import zio._

object ReceiverApp extends zio.App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    Server.server
      .provideSomeLayer[ZEnv](Server.appEnvironment)
      .exitCode
}
