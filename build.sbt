lazy val root = (project in file(".")).
  settings(
    name := "customer-events-demo",
    version := "1.0",
    scalaVersion := "2.11.7"
  )

val sparkVersion = "1.4.1"

val connectorVersion = "1.4.0"

val akkaVersion = "2.4.0"

val akkaHttpVersion = "2.0-M1"

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging"  %% "scala-logging"                          % "3.1.0",
  "com.typesafe"                %  "config"                                 % "1.3.0",
  "com.datastax.spark"          %% "spark-cassandra-connector"              % connectorVersion,
  "org.apache.spark"            %% "spark-core"                             % sparkVersion,
  "org.apache.spark"            %% "spark-streaming"                        % sparkVersion,
  "org.apache.spark"            %% "spark-sql"                              % sparkVersion,
  "org.apache.spark"            %% "spark-streaming-kafka"                  % sparkVersion,
  "com.typesafe.akka"           %% "akka-actor"                             % akkaVersion,
  "com.typesafe.akka"           %% "akka-http-experimental"                 % akkaHttpVersion,
  "com.typesafe.akka"           %% "akka-http-spray-json-experimental"      % akkaHttpVersion,
  "com.typesafe.akka"           %% "akka-testkit"                           % akkaVersion       % "test",
  "org.scalatest"               %% "scalatest"                              % "2.2.4"           % "test",
  "com.typesafe.akka"           %% "akka-http-testkit-experimental"         % akkaHttpVersion   % "test"
)