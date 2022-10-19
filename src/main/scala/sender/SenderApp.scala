package sender

import io.circe.syntax._
import org.apache.kafka.clients.producer.ProducerRecord
import sender.configuration._
import sender.eventgen.SubsBlockEvent
import zio._
import zio.blocking._
import zio.clock.Clock
import zio.duration.Duration
import zio.kafka.producer._
import zio.kafka.serde._
import zio.stream._


object SenderApp extends App {

  type ProducerEnv = Any with Blocking with Clock with Producer[Any, Long, String]

  override def run(args: List[String]) =
    SndConfig
      .load()
      .flatMap(
        config => produce(config).provideSomeLayer[Any with Blocking with Clock](createLayer(config))
      )
      .exitCode

  private def produce(sndConfig: SndConfig): ZIO[ProducerEnv, Throwable, Unit] =
    ZStream
      .repeat(SubsBlockEvent.from(sndConfig.dataGen.idLowerBound, sndConfig.dataGen.idUpperBound))
      .map(event => new ProducerRecord(sndConfig.kafkaClient.topic, event.id, event.asJson.toString))
      .mapM(
        Producer.produce[Any, Long, String](_) *>
          ZIO.sleep(Duration.fromMillis(sndConfig.dataGen.msBetweenMessages))
      )
      .runDrain

  private def createLayer(sndConfig: SndConfig) = {
    val producerSettings = ProducerSettings(sndConfig.brokers)
    val producerLayer = Producer.make[Any, Long, String](
      producerSettings, Serde.long, Serde.string
    ).toLayer
    producerLayer
  }

}
