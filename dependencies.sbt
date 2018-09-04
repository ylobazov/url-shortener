val scalaTestVersion    = "3.0.+"
val scalaCheckVersion   = "1.13.+"
val guavaVersion        = "26.0-jre"
val akkaHttpVersion     = "10.0.11"
//val jettyVersion        = "9.2.15.v20160210"

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
  "com.typesafe.akka"          %% "akka-http"                  % akkaHttpVersion,
//  "org.eclipse.jetty"          %  "jetty-webapp"               % jettyVersion             % "compile",
  "org.scalatest"              %% "scalatest"                  % scalaTestVersion         % "test, it",
  "org.scalacheck"             %% "scalacheck"                 % scalaCheckVersion        % "test, it"
)
