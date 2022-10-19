package receiver

import zio._
import zio.config.ReadError
import zio.config.typesafe.TypesafeConfig

package object configuration {

  case class RcvConfig(receiver: Receiver)
  case class Receiver(api: Api, liquibase: LiquibaseConfig)

  case class LiquibaseConfig(changeLog: String)
  case class Api(host: String, port: Int, uri: String)


  import zio.config.magnolia.DeriveConfigDescriptor.descriptor

  val configDescriptor: zio.config.ConfigDescriptor[RcvConfig] = descriptor[RcvConfig]

  type Configuration = zio.Has[RcvConfig]

  object Configuration{
    val live: Layer[ReadError[String], Configuration] = TypesafeConfig.fromDefaultLoader(configDescriptor)
  }

}
