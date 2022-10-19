package sender

import zio._
import zio.config.read
import zio.config.magnolia.DeriveConfigDescriptor
import zio.config.typesafe.TypesafeConfigSource
import com.typesafe.config.ConfigFactory

package object configuration {


  case class KafkaClient(bootstrapServers: String, topic: String)
  case class DataGen(idLowerBound: Long, idUpperBound: Long, msBetweenMessages: Int)

  final case class SndConfig(kafkaClient: KafkaClient, dataGen: DataGen) {
    def brokers: List[String] = kafkaClient.bootstrapServers.split(",").toList
  }

  object SndConfig {
    private val descriptor = DeriveConfigDescriptor.descriptor[SndConfig]

    def load(): Task[SndConfig] =
      for {
        rawConfig <- ZIO.effect(ConfigFactory.load().getConfig("sender"))
        configSource <- ZIO.fromEither(TypesafeConfigSource.fromTypesafeConfig(rawConfig))
        config <- ZIO.fromEither(read(SndConfig.descriptor.from(configSource)))
      } yield config
  }

}
