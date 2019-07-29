name := "imdb"

version := "0.1"

scalaVersion := "2.12.6"

val akkaVersion      = "2.5.19"
val akkaHttpVersion  = "10.1.7"
val scalaTestVersion = "3.0.5"

lazy val imdb = (project in file("."))
  .aggregate(core,service,web)

lazy val core = (project in file("core")).
  settings(
    libraryDependencies ++= Seq (
      "com.typesafe.akka"      %% "akka-actor"           % akkaVersion,
      "com.typesafe.akka"      %% "akka-slf4j"           % akkaVersion,
      "com.typesafe.akka"      %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka"      %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka"      %% "akka-stream"          % akkaVersion,
      "commons-validator"      % "commons-validator"     % "1.6",
      "com.github.mauricio"    %% "postgresql-async"     % "0.2.21",
      "ch.qos.logback"         %  "logback-classic"      % "1.2.3",
      "ch.qos.logback"         %  "logback-core"         % "1.2.1",
      "com.typesafe.akka"      %% "akka-testkit"         % akkaVersion      % Test,
      "org.scalatest"          %% "scalatest"            % scalaTestVersion % Test
    )
  )

lazy val web = (project in file("web")).
  settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka"   %% "akka-testkit"      % akkaVersion      % Test,
      "org.scalatest"       %%  "scalatest"        % scalaTestVersion % Test,
      "com.typesafe.akka"   %% "akka-http-testkit" % akkaHttpVersion  % Test
    )
  ).dependsOn(core,service)

lazy val service = (project in file("service")).
  settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka"    %% "akka-testkit"     % akkaVersion      % Test,
      "org.scalatest"        %% "scalatest"        % scalaTestVersion % Test
    )
  ).dependsOn(core)