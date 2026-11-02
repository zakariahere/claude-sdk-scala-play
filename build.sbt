name := """claudesdk-scala"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.18"
val jacksonVersion = "2.19.4"
val kotlinVersion  = "1.9.25"

Compile / scalacOptions += "-Wnonunit-statement"

libraryDependencies ++= Seq(guice, "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "org.typelevel" %% "cats-core" % "2.13.0",
  "com.anthropic" % "anthropic-java" % "2.73.0")


dependencyOverrides ++= Seq(
  "com.fasterxml.jackson.core"       %  "jackson-core"                   % jacksonVersion,
  "com.fasterxml.jackson.core"       %  "jackson-databind"               % jacksonVersion,
  "com.fasterxml.jackson.core"       %  "jackson-annotations"            % jacksonVersion,
  "com.fasterxml.jackson.datatype"   %  "jackson-datatype-jdk8"          % jacksonVersion,
  "com.fasterxml.jackson.datatype"   %  "jackson-datatype-jsr310"        % jacksonVersion,
  "com.fasterxml.jackson.dataformat" %  "jackson-dataformat-cbor"        % jacksonVersion,
  "com.fasterxml.jackson.module"     %  "jackson-module-parameter-names" % jacksonVersion,
  "com.fasterxml.jackson.module"     %% "jackson-module-scala"           % jacksonVersion,
  "com.fasterxml.jackson.module"     %  "jackson-module-kotlin"          % jacksonVersion,
  "org.jetbrains.kotlin" % "kotlin-stdlib"        % kotlinVersion,
  "org.jetbrains.kotlin" % "kotlin-stdlib-common" % kotlinVersion,
  "org.jetbrains.kotlin" % "kotlin-stdlib-jdk7"   % kotlinVersion,
  "org.jetbrains.kotlin" % "kotlin-stdlib-jdk8"   % kotlinVersion,
  "org.jetbrains.kotlin" % "kotlin-reflect"       % kotlinVersion
)
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
