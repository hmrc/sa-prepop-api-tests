ThisBuild / scalaVersion := "3.3.4"

lazy val testSuite = (project in file("."))
  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    name := "sa-pre-pop-api-tests",
    version := "2.0.0",
    scalacOptions += "-feature",
    libraryDependencies ++= AppDependencies()
  )

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt")
