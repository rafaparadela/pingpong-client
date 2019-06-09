package com.example.pingpong.client
package common

import scala.math.BigDecimal.RoundingMode
import com.example.pingpong.protocol._

final case class ClientConfig(name: String, host: String, port: Int)

final case class PingPongClientConfig(client: ClientConfig)

final case class TemperaturesSummary(
    temperatures: Vector[Temperature],
    averageTemperature: Temperature
) {
  def append(newTemperature: Temperature): TemperaturesSummary = {
    val temp = this.temperatures :+ newTemperature
    this.copy(
      temp,
      Temperature(
        BigDecimal(
          this.averageTemperature.value + (newTemperature.value - this.averageTemperature.value) / (this.temperatures.length + 1)
        ).setScale(2, RoundingMode.HALF_UP).toDouble,
        this.averageTemperature.unit
      )
    )
  }
}

object TemperaturesSummary {
  val empty: TemperaturesSummary =
    TemperaturesSummary(Vector.empty, Temperature(0d, TemperatureUnit("Fahrenheit")))
}
