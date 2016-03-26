name := """play-forms-tutorial"""
version := "1.0-SNAPSHOT"
scalaVersion := "2.11.7"
lazy val root = (project in file(".")).enablePlugins(PlayScala)
routesGenerator := InjectedRoutesGenerator
libraryDependencies ++= Seq(
  filters,
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.0",
  "io.megam" %% "newman" % "1.3.12"
)
resolvers ++= Seq(
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.bintrayRepo("scalaz", "releases"),
  Resolver.bintrayRepo("megamsys", "scala")
)
addSbtPlugin("com.heroku" % "sbt-heroku" % "0.5.1")
