import ProjectPlugin._

lazy val root = project.in(file("client"))

addCommandAlias("runClient", "runMain com.example.pingpong.client.app.ClientApp")
