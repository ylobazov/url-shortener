import sbt.ExclusionRule

val scalaTestVersion    = "3.0.+"
val scalaCheckVersion   = "1.13.+"
val guavaVersion        = "26.0-jre"
val akkaVersion         = "2.5.8"
val akkaHttpVersion     = "10.0.11"
val mongoVersion        = "2.4.1"

val logbackVersion     = "1.2.3"
val slf4jVersion       = "1.7.25"

resolvers in Global ++= Seq(
  "Maven Central Server"          at "http://repo1.maven.org/maven2",
  "MVNRepository"                 at "https://mvnrepository.com/artifact",
)

libraryDependencies                ++= Seq(
  "ch.qos.logback"             %  "logback-classic"            % logbackVersion,
  "org.slf4j"                  %  "slf4j-api"                  % slf4jVersion,
  "com.google.guava"           %  "guava"                      % guavaVersion,
  "com.typesafe.akka"          %%  "akka-stream"               % akkaVersion,
  "com.typesafe.akka"          %%  "akka-http"                 % akkaHttpVersion excludeAll(
    ExclusionRule(organization = "com.typesafe.akka", name = "akka-stream_2.12"),
    ExclusionRule(organization = "com.typesafe.akka", name = "akka-actor_2.12")
  ),
  "org.mongodb.scala"          %% "mongo-scala-driver"         % mongoVersion,
  "org.scalatest"              %% "scalatest"                  % scalaTestVersion         % "test, it",
  "org.scalacheck"             %% "scalacheck"                 % scalaCheckVersion        % "test, it",
  "com.typesafe.akka"          %% "akka-testkit"               % akkaVersion              % "test, it",
  "com.typesafe.akka"          %% "akka-http-testkit"          % akkaHttpVersion          % "test, it"
)
