name := "AntifraudInteraction"

version := "0.1"

scalaVersion := "2.13.8"

lazy val ZioVersion = "1.0.4"
lazy val PureconfigVersion = "0.12.3"
lazy val CatsEffectVersion = "3.3.12"
lazy val FS2Version = "2.4.2" //"3.2.10"
lazy val Http4sVersion = "0.21.7" //"1.0.0-M23"
lazy val DeclineVersion = "2.0.0"
lazy val CirceVersion = "0.14.1"

lazy val CatsEffectTestVersion = "1.1.1"
lazy val Specs2Version = "4.10.5"
lazy val ScalaCheckVersion = "1.15.1"

lazy val LiquibaseVersion = "3.4.2"
lazy val PostgresVersion = "42.2.8"

lazy val LogbackVersion = "1.2.3"

lazy val akkaVersion = "2.6.15"
lazy val leveldbVersion = "0.7"
lazy val leveldbjniVersion = "1.8"
lazy val cassandraVersion = "1.0.5"
lazy val json4sVersion = "3.2.11"
lazy val protobufVersion = "3.6.1"
lazy val scalikejdbc = "3.5.0"

libraryDependencies ++= Seq(

  "dev.zio" %% "zio" % ZioVersion,
  "dev.zio" %% "zio-streams" % ZioVersion,
  "dev.zio" %% "zio-kafka" % "0.13.0",
  "dev.zio" %% "zio-test" % ZioVersion,
  "dev.zio" %% "zio-test-sbt" % ZioVersion,
  "dev.zio" %% "zio-macros" % ZioVersion,

  "dev.zio" %% "zio-config" % ZioVersion,
  "dev.zio" %% "zio-config-magnolia" % ZioVersion,
  "dev.zio" %% "zio-config-typesafe" % ZioVersion,
  "dev.zio" %% "zio-interop-cats" % "2.2.0.1", // "3.1.1.0"

  "org.liquibase" % "liquibase-core" % LiquibaseVersion,
  "org.postgresql" % "postgresql" % PostgresVersion,
  "io.getquill" %% "quill-jdbc-zio" % "3.12.0",
  "io.github.kitlangton" %% "zio-magic" % "0.3.11",
  "org.scalikejdbc" %% "scalikejdbc" % scalikejdbc,

  "co.fs2" %% "fs2-core" % FS2Version,
  "co.fs2" %% "fs2-io" % FS2Version,

  "org.http4s" %% "http4s-dsl" % Http4sVersion,
  "org.http4s" %% "http4s-circe" % Http4sVersion,
  "org.http4s" %% "http4s-ember-server" % Http4sVersion,
  "org.http4s" %% "http4s-ember-client" % Http4sVersion,
  "org.http4s" %% "http4s-blaze-server"  % Http4sVersion,
  "org.http4s" %% "http4s-blaze-client"  % Http4sVersion,

  "com.github.pureconfig" %% "pureconfig" % PureconfigVersion,
  "com.github.pureconfig" %% "pureconfig-cats-effect" % PureconfigVersion,

  "com.monovore" %% "decline" % DeclineVersion,
  "io.circe" %% "circe-core" % CirceVersion,
  "io.circe" %% "circe-parser" % CirceVersion,
  "io.circe" %% "circe-generic" % CirceVersion,
  "io.circe" %% "circe-literal" % CirceVersion,

  "ch.qos.logback"  %  "logback-classic" % LogbackVersion,
  "com.typesafe" % "config" % "1.4.0",
  "org.typelevel" %% "kind-projector" % "0.10.3",

  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % "2.1.1",
  "com.typesafe.akka" %% "akka-http" % "10.2.4",

  "com.typesafe.akka" %% "akka-coordination" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,

  "com.typesafe.akka" %% "akka-persistence-cassandra" % cassandraVersion,
  "com.typesafe.akka" %% "akka-persistence-cassandra-launcher" % cassandraVersion,

  "com.typesafe.slick" %% "slick" % "3.3.3",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3",
  "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "3.0.3",

  "org.iq80.leveldb" % "leveldb" % leveldbVersion,
  "org.fusesource.leveldbjni" % "leveldbjni-all" % leveldbjniVersion,

  "org.scalatest" %% "scalatest" % "3.2.1" % Test,
  "org.scalatest" %% "scalatest-funsuite" % "3.2.1" % Test,
  "org.scalamock" %% "scalamock" % "5.1.0" % Test,

  "com.dimafeng"  %% "testcontainers-scala-postgresql" % "0.39.11"  % Test,
  "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.39.11"  % Test,

  "org.typelevel" %% "cats-effect-testing-specs2" % CatsEffectTestVersion % Test,
  "org.specs2" %% "specs2-core" % Specs2Version % Test,
  "org.scalacheck" %% "scalacheck" % ScalaCheckVersion % Test
)

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3")

useCoursier := false
scalacOptions += "-Ymacro-annotations"
scalacOptions += "-deprecation"

resolvers += Resolver.bintrayRepo("akka", "snapshots")

assemblyMergeStrategy in assembly := {
  case "META-INF/services/org.apache.spark.sql.sources.DataSourceRegister" => MergeStrategy.concat
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
