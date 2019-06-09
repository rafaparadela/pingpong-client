package com.example.pingpong.client.app

import cats.effect._
import cats.syntax.functor._
import com.example.pingpong.client.common._
import com.example.pingpong.client.process.PingPongServiceApi
import cats.effect.Effect
import cats.syntax.either._
import pureconfig.{ConfigReader, Derivation}
import fs2.Stream
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import pureconfig.generic.auto._

abstract class ClientBoot[F[_]: ConcurrentEffect: ContextShift: Timer] {

  def pingPongServiceApi(host: String, port: Int)(implicit L: Logger[F]): Stream[F, PingPongServiceApi[F]] =
    PingPongServiceApi.createInstance(host, port, sslEnabled = false)

  def runProgram(args: List[String]): Stream[F, ExitCode] = {

    for {
      config   <- Stream.eval(serviceConfig[ClientConfig].map(PingPongClientConfig))
      logger   <- Stream.eval(Slf4jLogger.fromName[F](config.client.name))
      exitCode <- clientProgram(config)(logger)
    } yield exitCode
  }

  def clientProgram(config: PingPongClientConfig)(implicit L: Logger[F]): Stream[F, ExitCode]

  def serviceConfig[Config](implicit reader: Derivation[ConfigReader[Config]]): F[Config] =
    Effect[F].fromEither(
      pureconfig
        .loadConfig[Config]
        .leftMap(e => new IllegalStateException(s"Error loading configuration: $e"))
    )


}


