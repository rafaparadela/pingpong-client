import higherkindness.mu.rpc.idlgen.IdlGenPlugin.autoImport._
import sbt.Keys._
import sbt._

object ProjectPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  object autoImport {

    lazy val V = new {
      val catsEffect     = "1.2.0"
      val log4cats       = "0.3.0"
      val logbackClassic = "1.2.3"
      val mu             = "0.18.0"
      val pureconfig     = "0.10.2"
      val circeVersion   = "0.11.1"
    }
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      name := "ping-pong-client",
      organization := "com.example",
      scalaVersion := "2.12.8",
      scalacOptions := Seq(
        "-deprecation",
        "-encoding",
        "UTF-8",
        "-feature",
        "-language:existentials",
        "-language:higherKinds",
        "-language:implicitConversions",
        "-unchecked",
        "-Xlint",
        "-Yno-adapted-args",
        "-Ywarn-dead-code",
        "-Ywarn-numeric-widen",
        "-Ywarn-value-discard",
        "-Xfuture",
        "-Ywarn-unused-import"
      ),
      idlType := "avro",
      srcGenSerializationType := "Avro",
      srcGenJarNames := Seq("ping-pong-protocol"),
      srcGenTargetDir := (Compile / sourceManaged).value / "compiled_avro",
      srcGenIDLTargetDir := (Compile / sourceManaged).value / "avro",
      sourceGenerators in Compile += (Compile / srcGen).taskValue,
      libraryDependencies ++= Seq(
        "ch.qos.logback"    % "logback-classic" % V.logbackClassic,
        "io.chrisdavenport" %% "log4cats-core"  % V.log4cats,
        "io.chrisdavenport" %% "log4cats-slf4j" % V.log4cats,
        "io.higherkindness" %% "mu-rpc-netty" % V.mu,
        "io.higherkindness" %% "mu-rpc-fs2"   % V.mu,
        "io.higherkindness" %% "mu-rpc-channel" % V.mu,
        "org.typelevel"         %% "cats-effect" % V.catsEffect,
        "com.github.pureconfig" %% "pureconfig"  % V.pureconfig,
//        "com.example" % "ping-pong-protocol" % "0.1.0"
      ),
      addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
    )
}
