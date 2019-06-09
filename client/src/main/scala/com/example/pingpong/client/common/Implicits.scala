package com.example.pingpong.client.common

import cats.Show
import cats.syntax.show._
import com.example.pingpong.protocol._

object Implicits {
  implicit val catsShowInstanceForPong: Show[Pong] =
    new Show[Pong] {
      override def show(pong: Pong): String = s"${pong.value}"
    }
}
