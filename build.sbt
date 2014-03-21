name := """akkaPattern"""

version := "1.0"

scalaVersion := "2.10.3"

libraryDependencies ++= akka

lazy val akka = Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.0",
  "com.typesafe.akka" %% "akka-kernel" % "2.3.0",
  "com.typesafe.akka" %% "akka-remote" % "2.3.0",
  "org.scalatest" %% "scalatest" % "2.0.M5b" % "test",
  "junit" % "junit" % "4.11" % "test",
  "com.novocode" % "junit-interface" % "0.10" % "test",
  "com.typesafe" % "config" % "1.2.0",
   "org.mockito" % "mockito-all" % "1.9.5" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.0" % "test",
  "redis.clients" % "jedis" % "2.1.0"
)

lazy val redis = Seq (
  "net.debasishg" % "redisclient_2.10" % "2.12"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")

lazy val clientServer = project.in( file("ClientServerExample") ).settings(
  libraryDependencies ++= akka
)

lazy val loadGenerate = project.in( file("loadGenerateExample")).settings(
  libraryDependencies ++= akka
)


lazy val akkaConcurrencyBook = project.in( file("akkaConcurrencyBook")).settings(
  libraryDependencies ++= akka
)

lazy val msgcoreAkkaExample = project.in(file("msgcoreAkkaExample")).settings(
   libraryDependencies ++= akka
)

lazy val zeusRedisScala = project.in(file("zeusRedisScala")).settings(
   libraryDependencies ++= akka
)

lazy val otherRedis = project.in(file("otherRedis")).settings(
   libraryDependencies ++= (akka ++ redis)
)

