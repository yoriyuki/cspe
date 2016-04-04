
lazy val commonSettings = Seq(
  organization := "jp.go.aist.cspe",
  version := "0.4.0",
  scalaVersion := "2.11.8"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "csp_e",
    resolvers ++= Seq(
      "LocalRepo" at "file://" + file(Path.userHome.absolutePath + "/.ivy2/local").getAbsolutePath
    ))