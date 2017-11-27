name := """form3"""
organization := "form3"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

libraryDependencies += guice

libraryDependencies ++= Seq(
  evolutions,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0",
  "com.typesafe.play" %% "play-slick" % "3.0.2",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.2",
  "org.postgresql" % "postgresql" % "42.1.4.jre7",
  "org.mockito" % "mockito-core" % "2.12.0"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "form3.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "form3.binders._"

resolvers += "Spy Repository" at "http://files.couchbase.com/maven2" // required to resolve `spymemcached`, the plugin's dependency.
resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"