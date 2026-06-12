ThisBuild / scalaVersion := "3.8.4"

lazy val root = (project in file("."))
  .settings(
    name := "gol_scala",
    idePackagePrefix := Some("org.example")
  )
