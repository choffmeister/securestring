name := "securestring"

version := "0.0.1"

organization := "de.choffmeister"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.11" % "test",
  "org.specs2" %% "specs2" % "2.2.3" % "test"
)

testOptions in Test += Tests.Argument("junitxml", "console")

publishMavenStyle := true

publishTo := Some(Resolver.sftp("repo.choffmeister.de", "choffmeister.de", "/var/www/de.choffmeister.repo/maven2"))

ScctPlugin.instrumentSettings

CoveragePlugin.coverageSettings

EclipseKeys.withSource := true
