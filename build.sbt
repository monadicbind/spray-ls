organization  := "com.example"

version       := "0.1"

scalaVersion  := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.4-M2"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "org.json4s"          %%  "json4s-native"  % "3.2.11",
    "org.json4s"          %%  "json4s-jackson"  % "3.2.11",
    "io.spray"            %%  "spray-json" % "1.3.2",
    "mysql"               %   "mysql-connector-java" % "5.1.36",
    "com.typesafe.slick" %% "slick" % "3.0.1",
    "ch.qos.logback" % "logback-classic" % "1.0.9",
    //"org.slf4j" % "slf4j-nop" % "1.6.4",
    "com.typesafe.akka" %% "akka-slf4j" % "2.4-M2"
  )
}

Revolver.settings
