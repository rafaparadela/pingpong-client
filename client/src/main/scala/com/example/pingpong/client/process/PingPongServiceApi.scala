package com.example.pingpong.client.process

import java.net.InetAddress

import cats.effect._
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.example.pingpong.client.common._
import com.example.pingpong.protocol._
import com.example.pingpong.protocol.PingPongService
import fs2.Stream
import io.chrisdavenport.log4cats.Logger
import higherkindness.mu.rpc.ChannelForAddress
import higherkindness.mu.rpc.channel.{ManagedChannelInterpreter, UsePlaintext}
import io.grpc.{CallOptions, ManagedChannel}

trait PingPongServiceApi[F[_]] {
  def pingPong(request: Stream[F, Ping]): Stream[F, Pong]
}

object PingPongServiceApi {

  def apply[F[_]: Effect](clientRPC: PingPongService[F])(implicit L: Logger[F]): PingPongServiceApi[F] =
    new PingPongServiceApi[F] {

      override def pingPong(request: Stream[F, Ping]): Stream[F, Pong] =
        clientRPC.pingPong(request)
    }

  def createInstance[F[_]: ContextShift: Logger: Timer](
      hostname: String,
      port: Int,
      sslEnabled: Boolean = true
  )(implicit F: ConcurrentEffect[F]): fs2.Stream[F, PingPongServiceApi[F]] = {

    val channel: F[ManagedChannel] =
      F.delay(InetAddress.getByName(hostname).getHostAddress).flatMap { ip =>
        val channelFor    = ChannelForAddress(ip, port)
        val channelConfig = if (!sslEnabled) List(UsePlaintext()) else Nil
        new ManagedChannelInterpreter[F](channelFor, channelConfig).build
      }

    def clientFromChannel: Resource[F, PingPongService[F]] =
      PingPongService.clientFromChannel(channel, CallOptions.DEFAULT)

    fs2.Stream.resource(clientFromChannel).map(PingPongServiceApi(_))
  }
}
