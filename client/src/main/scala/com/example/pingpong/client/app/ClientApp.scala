package com.example.pingpong.client.app

import cats.effect._
import cats.implicits._
import com.example.pingpong.client.common._
import com.example.pingpong.client.common.Implicits._
import fs2.Stream
import io.chrisdavenport.log4cats.Logger

class ClientProgram[F[_]: ConcurrentEffect: ContextShift: Timer] extends ClientBoot[F] {

  def clientProgram(config: PingPongClientConfig)(implicit L: Logger[F]): Stream[F, ExitCode] = {
    for {
      serviceApi <- pingPongServiceApi(config.client.host, config.client.port)
      _          <- Stream.eval(serviceApi.isEmpty)
      summary    <- serviceApi.getTemperature
      _          <- Stream.eval(Logger[F].info(s"The average temperature is: ${summary.averageTemperature}"))
      response   <- serviceApi.comingBackMode(LocationsGenerator.get[F])
      _          <- Stream.eval(Logger[F].info(response.show))
    } yield ExitCode.Success
  }
}

object ClientApp extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    new ClientProgram[IO]
      .runProgram(args)
      .compile
      .toList
      .map(_.headOption.getOrElse(ExitCode.Error))
}
