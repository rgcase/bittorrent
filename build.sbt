lazy val root = (project in file("."))
  .settings(projectSettings: _*)
  .settings(libraryDependencies ++= projectDependencies)
  .settings(headerSettings: _*)

lazy val projectSettings = Seq(
  name := "bittorrent",
  version := "0.1.0",
  organization := "com.rgcase",
  scalaVersion := "2.12.2",
  cancelable in Global := true
)

lazy val headerSettings = Seq(
  // sbt createHeaders
  organizationName := "com.rgcase",
  startYear := Some(2017),
  licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))
)

lazy val projectDependencies = Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.3",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.3" % Test,

  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
)